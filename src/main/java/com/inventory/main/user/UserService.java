package com.inventory.main.user;

import com.inventory.main.location.Location;
import com.inventory.main.movement.Coordination;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

  private UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = this.userRepository.findByEmailOrPhone(email);

    if (user == null) {
      throw new UsernameNotFoundException("Пользователь не найден");
    }

    return user;
  }

  public Set<User> getAllActive() {
    return userRepository.findAllByDeletedAtIsNullOrderByLastNameAscFirstNameAsc();
  }

  public Set<User> getAllInactive() {
    return userRepository.findAllByDeletedAtIsNotNullOrderByLastNameAscFirstNameAsc();
  }

  public Optional<User> getById(int userId) {
    return userRepository.findById(userId);
  }

  public Iterable<User> getUsersByLocations(Set<Integer> locations) {
    return userRepository.findByLocationIdInAndDeletedAtIsNullOrderByLastNameAscFirstNameAsc(locations);
  }

  public User create(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return userRepository.save(user);
  }

  public User update(User user, User newUserData) {
    user.setLastName(newUserData.getLastName());
    user.setFirstName(newUserData.getFirstName());
    user.setEmail(newUserData.getEmail());
    user.setPhone(newUserData.getPhone());
    user.setLocationId(newUserData.getLocationId());
    user.setIsActive(newUserData.getIsActive());
    user.setRole(newUserData.getRole());

    if (org.springframework.util.StringUtils.hasText(newUserData.getPassword())) {
      user.setPassword(passwordEncoder.encode(newUserData.getPassword()));
    } else {
      user.setPassword(user.getPassword());
    }

    return userRepository.save(user);
  }

  public User updateProfile(User user, User newUserData) {
    user.setLastName(newUserData.getLastName());
    user.setFirstName(newUserData.getFirstName());
    user.setEmail(newUserData.getEmail());
    user.setPhone(newUserData.getPhone());

    if (org.springframework.util.StringUtils.hasText(newUserData.getPassword())) {
      user.setPassword(passwordEncoder.encode(newUserData.getPassword()));
    } else {
      user.setPassword(user.getPassword());
    }

    return userRepository.save(user);
  }

  @Transactional
  public void delete(User user) throws Exception {
    String exceptionsString = "";

    Set<Location> locations = user.getLocations();

    if (!locations.isEmpty()) {
      exceptionsString += "Пользователь является ответственным за подразделения: ";

      exceptionsString += locations.stream().map(Location::getTitle).toList().toString();
    }

    if (!exceptionsString.isEmpty()) {
      throw new Exception(exceptionsString);
    }

    Set<Coordination> coordinations = user.getCoordinations().stream().filter(coordination -> coordination.getStatus() == Coordination.Status.WAITING).collect(Collectors.toSet());

    if (!coordinations.isEmpty()) {
      Integer chiefUserId = null;
      Location parentLocation = user.getLocation();

      while (chiefUserId == null) {
        if (parentLocation.getResponsibleUserId() != null && parentLocation.getResponsibleUserId().equals(user.getId())) {
          chiefUserId = parentLocation.getParent().getResponsibleUserId();
        } else if (parentLocation.getResponsibleUserId() != null) {
          chiefUserId = parentLocation.getResponsibleUserId();
        }

        parentLocation = parentLocation.getParent();
      }

      for (Coordination coordination : coordinations) {
        coordination.setChiefUserId(chiefUserId);
      }
    }

    user.setDeletedAt(new Timestamp(new Date().getTime()));

    userRepository.save(user);
  }

  public void restore(int id) {
    userRepository.restoreById(id);
  }

}
