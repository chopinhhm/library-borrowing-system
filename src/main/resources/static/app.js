const state={auth:'',me:null,books:[],readers:[],types:[],loans:[],reservations:[],reminders:[],accounts:[]};
const $=s=>document.querySelector(s), $$=s=>[...document.querySelectorAll(s)];
const contextPath=location.pathname.startsWith('/library')?'/library':'';

async function api(path,options={}){
  const headers={Authorization:`Basic ${state.auth}`,...(options.body?{'Content-Type':'application/json'}:{}),...(options.headers||{})};
  const res=await fetch(`${contextPath}${path}`,{...options,headers});
  if(!res.ok){let msg=`请求失败（${res.status}）`;try{const body=await res.json();msg=body.message||msg}catch(e){}throw new Error(msg)}
  const text=await res.text();
  return text?JSON.parse(text):null;
}

function admin(){return state.me&&state.me.role==='ADMIN'}
function notify(message,error=false){$('#notice').innerHTML=`<div class="notice ${error?'error-note':''}">${escapeHtml(message)}</div>`;setTimeout(()=>$('#notice').innerHTML='',3500)}
function escapeHtml(v){return String(v??'').replace(/[&<>'"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]))}
function status(text,kind=''){return `<span class="status ${kind}">${escapeHtml(text)}</span>`}
function empty(cols,text='暂无数据'){return `<tr><td colspan="${cols}" class="empty">${text}</td></tr>`}
function todayIso(){const d=new Date();return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`}
function isOverdue(loan){return loan.status==='BORROWED'&&loan.dueAt<todayIso()}

$('#loginForm').addEventListener('submit',async e=>{
  e.preventDefault();
  state.auth=btoa(`${$('#username').value}:${$('#password').value}`);
  try{
    state.me=await api('/api/auth/me');
    localStorage.setItem('libraryAuth',state.auth);
    enterApp();
  }catch(err){$('#loginError').textContent='用户名或密码错误';state.auth=''}
});

function enterApp(){
  $('#loginView').classList.add('hidden');$('#app').classList.remove('hidden');
  $('#currentUser').textContent=state.me.username;$('#currentRole').textContent=admin()?'管理员':'读者';
  $$('[data-admin]').forEach(el=>el.classList.toggle('hidden',!admin()));
  showView('dashboard');
}

$('#logoutBtn').onclick=()=>{localStorage.removeItem('libraryAuth');location.reload()};
$('#nav').onclick=e=>{const b=e.target.closest('[data-view]');if(b)showView(b.dataset.view)};
document.body.addEventListener('click',e=>{const b=e.target.closest('[data-go]');if(b)showView(b.dataset.go)});

const meta={dashboard:['系统概览','查看图书馆当前运行状态'],books:['图书管理','检索和维护馆藏资料'],readers:['读者管理','维护读者资料和借阅规则'],circulation:['借阅流通','办理借书、还书、续借和预约'],accounts:['账号权限','管理登录账号和角色'],logs:['操作日志','追踪系统接口访问记录']};
async function showView(name){
  if(!admin()&&['readers','accounts','logs'].includes(name))name='dashboard';
  $$('.view').forEach(v=>v.classList.toggle('active',v.id===name));
  $$('#nav button').forEach(v=>v.classList.toggle('active',v.dataset.view===name));
  $('#pageTitle').textContent=meta[name][0];$('#pageSubtitle').textContent=meta[name][1];
  try{if(name==='dashboard')await loadDashboard();if(name==='books')await loadBooks();if(name==='readers')await loadReaders();if(name==='circulation')await loadCirculation();if(name==='accounts')await loadAccounts();if(name==='logs')await loadLogs()}catch(e){notify(e.message,true)}
}

async function loadDashboard(){
  let values;
  if(admin())values=await api('/api/admin/statistics');
  else{const [books,loans]=await Promise.all([api('/api/books'),api(`/api/circulation/readers/${state.me.readerId}/loans`)]);values={bookTitles:books.length,readers:'-',activeLoans:loans.filter(x=>x.status==='BORROWED').length,returnedLoans:loans.filter(x=>x.status==='RETURNED').length,reminders:'-'}}
  const cards=[['馆藏书目',values.bookTitles],['读者数量',values.readers],['当前在借',values.activeLoans],['已归还',values.returnedLoans],['催还记录',values.reminders]];
  $('#stats').innerHTML=cards.map(x=>`<div class="stat"><small>${x[0]}</small><strong>${x[1]}</strong></div>`).join('');
}

async function loadBooks(){
  const params=new URLSearchParams();
  if($('#bookKeyword').value.trim())params.set('keyword',$('#bookKeyword').value.trim());
  if($('#bookCategory').value.trim())params.set('category',$('#bookCategory').value.trim());
  if($('#bookShelf').value.trim())params.set('shelfLocation',$('#bookShelf').value.trim());
  if($('#availableOnly').checked)params.set('availableOnly','true');
  state.books=await api(`/api/books${params.toString()?`?${params}`:''}`);
  $('#bookRows').innerHTML=state.books.length?state.books.map(b=>`<tr><td>${b.id}</td><td>${escapeHtml(b.isbn)}</td><td>${escapeHtml(b.title)}</td><td>${escapeHtml(b.author)}</td><td>${escapeHtml(b.category)}</td><td>${escapeHtml(b.shelfLocation)}</td><td>${b.availableCopies}/${b.totalCopies}</td>${admin()?`<td><div class="actions"><button onclick="editBook(${b.id})">编辑</button><button onclick="deleteBook(${b.id})">删除</button></div></td>`:''}</tr>`).join(''):empty(admin()?8:7);
}
$('#searchBooks').onclick=()=>loadBooks().catch(e=>notify(e.message,true));
$('#resetBooks').onclick=()=>{['bookKeyword','bookCategory','bookShelf'].forEach(id=>$(`#${id}`).value='');$('#availableOnly').checked=false;loadBooks().catch(e=>notify(e.message,true))};
$('#addBookBtn').onclick=()=>bookModal();
function bookModal(book={}){openModal(book.id?'编辑图书':'新增图书',`
  ${field('isbn','ISBN',book.isbn||'','required')}${field('title','书名',book.title||'','required')}
  ${field('author','作者',book.author||'')}${field('category','分类',book.category||'')}
  ${field('shelfLocation','书架位置',book.shelfLocation||'')}${field('totalCopies','馆藏数量',book.totalCopies||1,'required min="1"','number')}`,
  async f=>{const body=Object.fromEntries(f);body.totalCopies=Number(body.totalCopies);await api(book.id?`/api/admin/books/${book.id}`:'/api/admin/books',{method:book.id?'PUT':'POST',body:JSON.stringify(body)});notify('图书资料已保存');await loadBooks()})}
window.editBook=id=>bookModal(state.books.find(x=>x.id===id));
window.deleteBook=async id=>{if(!confirm('确认删除这本图书？'))return;try{await api(`/api/admin/books/${id}`,{method:'DELETE'});notify('图书已删除');loadBooks()}catch(e){notify(e.message,true)}};

async function loadReaders(){
  [state.readers,state.types]=await Promise.all([api('/api/admin/readers'),api('/api/reader-types')]);
  $('#readerRows').innerHTML=state.readers.length?state.readers.map(r=>`<tr><td>${r.id}</td><td>${escapeHtml(r.cardNumber)}</td><td>${escapeHtml(r.name)}</td><td>${escapeHtml(r.email)}</td><td>${escapeHtml(r.readerType.name)}</td><td>${r.status==='ACTIVE'?status('正常'):status('暂停','off')}</td><td><button onclick="editReader(${r.id})">编辑</button></td></tr>`).join(''):empty(7);
  $('#typeRows').innerHTML=state.types.map(t=>`<tr><td>${t.id}</td><td>${escapeHtml(t.name)}</td><td>${t.maxBooks} 本</td><td>${t.loanDays} 天</td><td>${t.maxRenewals} 次</td><td>¥${t.dailyFineRate}/天</td></tr>`).join('')||empty(6);
}
$('#addReaderBtn').onclick=()=>readerModal();$('#addTypeBtn').onclick=()=>typeModal();
function readerModal(reader={}){const options=state.types.map(t=>`<option value="${t.id}" ${reader.readerType&&reader.readerType.id===t.id?'selected':''}>${escapeHtml(t.name)}</option>`).join('');openModal(reader.id?'编辑读者':'新增读者',`
  ${field('cardNumber','借阅证号',reader.cardNumber||'','required')}${field('name','姓名',reader.name||'','required')}
  ${field('email','邮箱',reader.email||'','','email')}<label>读者类型<select name="readerTypeId">${options}</select></label>
  <label>状态<select name="status"><option value="ACTIVE">正常</option><option value="SUSPENDED" ${reader.status==='SUSPENDED'?'selected':''}>暂停</option></select></label>`,
  async f=>{const body=Object.fromEntries(f);body.readerTypeId=Number(body.readerTypeId);await api(reader.id?`/api/admin/readers/${reader.id}`:'/api/admin/readers',{method:reader.id?'PUT':'POST',body:JSON.stringify(body)});notify('读者资料已保存');await loadReaders()})}
window.editReader=id=>readerModal(state.readers.find(x=>x.id===id));
function typeModal(){openModal('新增读者类型',`${field('name','类型名称','','required')}${field('maxBooks','最大借阅数',5,'required min="1"','number')}${field('loanDays','借期天数',30,'required min="1"','number')}${field('maxRenewals','最大续借次数',1,'required min="0"','number')}${field('dailyFineRate','每日罚金',0.5,'required min="0" step="0.01"','number')}`,async f=>{const body=Object.fromEntries(f);['maxBooks','loanDays','maxRenewals','dailyFineRate'].forEach(k=>body[k]=Number(body[k]));await api('/api/admin/reader-types',{method:'POST',body:JSON.stringify(body)});notify('借阅规则已新增');await loadReaders()})}

async function ensureCatalog(){if(!state.books.length)state.books=await api('/api/books');if(admin()&&!state.readers.length)state.readers=await api('/api/admin/readers')}
async function loadCirculation(){
  await ensureCatalog();
  if(admin()){
    const params=new URLSearchParams();
    if($('#loanStatus').value)params.set('status',$('#loanStatus').value);
    if($('#overdueOnly').checked)params.set('overdueOnly','true');
    [state.loans,state.reservations,state.reminders]=await Promise.all([api(`/api/circulation/admin/loans${params.toString()?`?${params}`:''}`),api('/api/circulation/admin/reservations'),api('/api/circulation/admin/reminders')]);
  }else{
    [state.loans,state.reservations]=await Promise.all([api(`/api/circulation/readers/${state.me.readerId}/loans`),api(`/api/circulation/readers/${state.me.readerId}/reservations`)]);
    if($('#loanStatus').value)state.loans=state.loans.filter(l=>l.status===$('#loanStatus').value);
    if($('#overdueOnly').checked)state.loans=state.loans.filter(isOverdue);
  }
  $('#loanRows').innerHTML=state.loans.length?state.loans.map(l=>{const overdue=isOverdue(l);return `<tr><td>${l.id}</td><td>${escapeHtml(l.reader.name)}</td><td>${escapeHtml(l.book.title)}</td><td>${l.borrowedAt}</td><td>${l.dueAt}</td><td>${l.status==='BORROWED'?status(overdue?'已逾期':'在借',overdue?'warn':''):status('已归还')}</td><td>¥${l.fine||0}</td><td><div class="actions">${l.status==='BORROWED'?`<button onclick="renewLoan(${l.id})">续借</button>${admin()?`<button onclick="returnLoan(${l.id})">还书</button>${overdue?`<button onclick="remindLoan(${l.id})">催还</button>`:''}`:''}`:''}</div></td></tr>`}).join(''):empty(8);
  $('#reservationRows').innerHTML=state.reservations.length?state.reservations.map(r=>`<tr><td>${r.id}</td><td>${escapeHtml(r.reader.name)}</td><td>${escapeHtml(r.book.title)}</td><td>${r.createdAt.replace('T',' ')}</td><td>${status(r.status==='ACTIVE'?'预约中':r.status)}</td><td>${r.status==='ACTIVE'?`<button onclick="cancelReservation(${r.id})">取消</button>`:''}</td></tr>`).join(''):empty(6);
  if(admin())$('#reminderRows').innerHTML=state.reminders.length?state.reminders.map(r=>`<tr><td>${r.id}</td><td>${escapeHtml(r.loan.reader.name)}</td><td>${escapeHtml(r.loan.book.title)}</td><td>${escapeHtml(r.recipient||'-')}</td><td>${escapeHtml(r.message)}</td><td>${r.sentAt.replace('T',' ')}</td></tr>`).join(''):empty(6);
}
$('#borrowBtn').onclick=()=>circulationModal('办理借书','/api/circulation/borrow');
$('#reserveBtn').onclick=()=>circulationModal('办理预约','/api/circulation/reserve');
$('#filterLoans').onclick=()=>loadCirculation().catch(e=>notify(e.message,true));
async function circulationModal(title,path){await ensureCatalog();const readerId=admin()?`<label>读者<select name="readerId">${state.readers.map(r=>`<option value="${r.id}">${escapeHtml(r.name)}（${escapeHtml(r.cardNumber)}）</option>`).join('')}</select></label>`:`<input type="hidden" name="readerId" value="${state.me.readerId}">`;openModal(title,`${readerId}<label>图书<select name="bookId">${state.books.map(b=>`<option value="${b.id}">${escapeHtml(b.title)}（可借 ${b.availableCopies}）</option>`).join('')}</select></label>`,async f=>{const x=Object.fromEntries(f);await api(`${path}?readerId=${x.readerId}&bookId=${x.bookId}`,{method:'POST'});notify(`${title}成功`);await loadCirculation()})}
window.renewLoan=async id=>act(`/api/circulation/loans/${id}/renew`,'续借成功');
window.returnLoan=async id=>act(`/api/circulation/loans/${id}/return`,'还书成功');
window.remindLoan=async id=>act(`/api/circulation/admin/loans/${id}/remind`,'催还记录已创建');
window.cancelReservation=async id=>act(`/api/circulation/reservations/${id}/cancel`,'预约已取消');
async function act(path,msg){try{await api(path,{method:'POST'});notify(msg);await loadCirculation()}catch(e){notify(e.message,true)}}

async function loadAccounts(){state.accounts=await api('/api/admin/accounts');$('#accountRows').innerHTML=state.accounts.map(a=>`<tr><td>${a.id}</td><td>${escapeHtml(a.username)}</td><td>${a.role==='ADMIN'?status('管理员'):status('读者')}</td><td>${a.readerId||'-'}</td><td>${a.enabled?status('启用'):status('停用','off')}</td><td><button onclick="toggleAccount(${a.id})">${a.enabled?'停用':'启用'}</button></td></tr>`).join('')||empty(6)}
$('#addAccountBtn').onclick=async()=>{if(!state.readers.length)state.readers=await api('/api/admin/readers');openModal('新增登录账号',`${field('username','用户名','','required')}${field('password','初始密码','','required minlength="6"','password')}<label>角色<select name="role"><option value="READER">读者</option><option value="ADMIN">管理员</option></select></label><label>关联读者<select name="readerId"><option value="">不关联</option>${state.readers.map(r=>`<option value="${r.id}">${escapeHtml(r.name)}</option>`).join('')}</select></label>`,async f=>{const body=Object.fromEntries(f);body.readerId=body.readerId?Number(body.readerId):null;await api('/api/admin/accounts',{method:'POST',body:JSON.stringify(body)});notify('账号创建成功');await loadAccounts()})};
window.toggleAccount=async id=>{const a=state.accounts.find(x=>x.id===id);try{await api(`/api/admin/accounts/${id}`,{method:'PATCH',body:JSON.stringify({enabled:!a.enabled,role:a.role,readerId:a.readerId})});notify('账号状态已更新');loadAccounts()}catch(e){notify(e.message,true)}};

async function loadLogs(){const logs=await api('/api/admin/logs?limit=200');$('#logRows').innerHTML=logs.map(l=>`<tr><td>${(l.operatedAt||'').replace('T',' ')}</td><td>${escapeHtml(l.username)}</td><td>${l.method}</td><td>${escapeHtml(l.path)}</td><td>${l.statusCode}</td></tr>`).join('')||empty(5)}

function field(name,label,value='',attrs='',type='text'){return `<label>${label}<input name="${name}" type="${type}" value="${escapeHtml(value)}" ${attrs}></label>`}
function openModal(title,body,onSave){$('#modalTitle').textContent=title;$('#modalBody').innerHTML=body;$('#modalForm').onsubmit=async e=>{e.preventDefault();try{await onSave(new FormData(e.target));$('#modal').close()}catch(err){notify(err.message,true)}};$('#modal').showModal()}
$$('[data-close-modal]').forEach(b=>b.onclick=()=>$('#modal').close());

setInterval(()=>$('#clock').textContent=new Date().toLocaleString('zh-CN'),1000);
(async()=>{const saved=localStorage.getItem('libraryAuth');if(saved){state.auth=saved;try{state.me=await api('/api/auth/me');enterApp()}catch(e){localStorage.removeItem('libraryAuth')}}})();
