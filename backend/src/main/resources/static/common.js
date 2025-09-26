// common.js - simple auth helper and fetch wrapper
const API = {
  getAuthToken() { return localStorage.getItem('iesadep_token'); },
  setAuthToken(t) { if(t) localStorage.setItem('iesadep_token', t); else localStorage.removeItem('iesadep_token'); },
  authHeaders() {
    const t = API.getAuthToken();
    return t ? { 'Authorization': 'Bearer ' + t } : {};
  },
  fetchJson(url, opts={}) {
    opts.headers = Object.assign(opts.headers || {}, {'Content-Type':'application/json'}, API.authHeaders());
    return fetch(url, opts).then(async r => {
      if(!r.ok) throw new Error('HTTP '+r.status);
      return r.json();
    });
  }
};

function renderLoginArea(){
  const container = document.getElementById('login-area');
  container.innerHTML = '';
  const token = API.getAuthToken();
  if(!token){
    const btn = document.createElement('button');
    btn.className='btn btn-sm btn-primary';
    btn.innerText='Iniciar sesión (admin)';
    btn.onclick = ()=> showLoginModal();
    container.appendChild(btn);
  } else {
    const btn = document.createElement('button');
    btn.className='btn btn-sm btn-danger';
    btn.innerText='Cerrar sesión';
    btn.onclick = ()=> { API.setAuthToken(null); renderLoginArea(); };
    container.appendChild(btn);
  }
}

function showLoginModal(){
  const html = `<div class="modal fade" id="loginModal" tabindex="-1">
  <div class="modal-dialog modal-sm">
    <div class="modal-content">
      <div class="modal-header"><h5 class="modal-title">Login admin</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
      <div class="modal-body">
        <div class="mb-2"><input id="login-user" class="form-control" placeholder="Usuario"></div>
        <div class="mb-2"><input id="login-pass" type="password" class="form-control" placeholder="Contraseña"></div>
        <div id="login-error" class="text-danger"></div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-primary" id="login-send">Entrar</button>
      </div>
    </div>
  </div>
</div>`;
  document.body.insertAdjacentHTML('beforeend', html);
  const modal = new bootstrap.Modal(document.getElementById('loginModal'));
  modal.show();
  document.getElementById('login-send').onclick = async ()=>{
    const user = document.getElementById('login-user').value;
    const pass = document.getElementById('login-pass').value;
    try{
      const res = await fetch('/api/auth/login', {
        method:'POST',
        headers:{'Content-Type':'application/json'},
        body: JSON.stringify({username:user,password:pass})
      });
      if(!res.ok) throw new Error('Credenciales inválidas');
      const j = await res.json();
      API.setAuthToken(j.token);
      modal.hide();
      renderLoginArea();
    }catch(e){
      document.getElementById('login-error').innerText = 'Falló login';
      console.error(e);
    }
  };
}
