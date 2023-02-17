/**
 * Login page
 */
if (document.getElementById('login-form')) {
  document.getElementById('owner-btn').addEventListener('click', (e) => {
    login('elizaveta2042@ya.ru', '1234');
  });
  document.getElementById('admin-btn').addEventListener('click', (e) => {
    login('admin@example.com', '1');
  });
  document.getElementById('responsible-btn').addEventListener('click', (e) => {
    login('vasiliy16011990@yandex.ru', '1');
  });
  document.getElementById('user-btn').addEventListener('click', (e) => {
    login('nikita91@ya.ru', '1');
  });


  function login(login, pass) {
    document.getElementById('login').value = login;
    document.getElementById('password').value = pass;

    document.getElementById('login-form').submit();
  }
}
