// All data comes from the Spring Boot REST API; nothing here fakes a backend response.
let role = 'PASSENGER';
let selected = null;   // flight id

const $ = id => document.getElementById(id);

async function api(method, path, body) {
  const res = await fetch('/api' + path, {
    method,
    headers: { 'Content-Type': 'application/json', 'X-Role': role },
    body: body ? JSON.stringify(body) : undefined
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.error || ('Request failed (' + res.status + ')'));
  return data;
}

function flash(text, ok) {
  const m = $('msg');
  m.textContent = text;
  m.className = 'msg ' + (ok ? 'ok' : 'err');
  clearTimeout(flash.t);
  flash.t = setTimeout(() => m.className = 'msg hidden', 6000);
}

function show(title, lines) {
  $('result').textContent = title + '\n' + (Array.isArray(lines) ? lines.join('\n') : lines);
}

function esc(s) { return String(s ?? '').replace(/[&<>"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c])); }

// ---------- login ----------
$('loginBtn').onclick = async () => {
  try {
    const u = await api('POST', '/auth/login', { username: $('user').value, password: $('pass').value });
    role = u.role;
    $('who').textContent = 'Logged in: ' + u.username + ' (' + role + ')';
    $('loginBtn').classList.add('hidden'); $('logoutBtn').classList.remove('hidden');
    flash('Welcome ' + u.username, true);
    applyRole();
  } catch (e) { flash(e.message, false); }
};
$('logoutBtn').onclick = () => {
  role = 'PASSENGER';
  $('who').textContent = 'Viewing as PASSENGER (read-only)';
  $('loginBtn').classList.remove('hidden'); $('logoutBtn').classList.add('hidden');
  applyRole();
};
function applyRole() {
  const staff = role === 'STAFF' || role === 'ADMIN';
  document.querySelectorAll('[data-act]').forEach(b => b.disabled = !staff || !selected);
}

// ---------- rendering ----------
async function loadFlights() {
  const flights = await api('GET', '/flights');
  $('flights').innerHTML = flights.map(f => `
    <tr class="${f.flightId === selected ? 'sel' : ''}">
      <td>${esc(f.flightId)}</td><td><b>${esc(f.flightNumber)}</b></td><td>${esc(f.airline)}</td>
      <td>${esc(f.category)}</td><td>${esc(f.source)} &rarr; ${esc(f.destination)}</td>
      <td>${esc(f.landingTime)}</td><td>${esc(f.gate || '-')}</td>
      <td><span class="badge ${esc(f.status)}">${esc(f.status)}</span></td>
      <td>${esc(f.baggageBelt || '-')}</td><td>${esc(f.baggageArrivalSlot || '-')}</td>
      <td><button onclick="selectFlight('${esc(f.flightId)}')">Select</button></td>
    </tr>`).join('');
  if (selected) {
    const f = flights.find(x => x.flightId === selected);
    $('selLabel').textContent = f ? f.flightNumber + ' (' + f.flightId + ')' : 'select a flight';
  }
}
async function loadNotes() {
  const notes = await api('GET', '/notifications');
  $('notes').innerHTML = notes.map(n => '<li>' + esc(n) + '</li>').join('') || '<li>No notifications yet.</li>';
}
async function loadHistory() {
  if (!selected) { $('history').innerHTML = '<li>Select a flight.</li>'; return; }
  const h = await api('GET', '/flights/' + selected + '/history');
  $('history').innerHTML = h.map(x => '<li>' + esc(x) + '</li>').join('') || '<li>No saved states yet.</li>';
}
async function loadBelts() {
  const belts = await api('GET', '/belts');
  const opts = Object.keys(belts).map(b => `<option value="${b}">${b}${belts[b] ? ' (occupied)' : ''}</option>`).join('');
  const keep = $('a_belt').value;
  $('a_belt').innerHTML = opts; if (keep) $('a_belt').value = keep;
  const c = $('c_belt'); const ck = c.value;
  c.innerHTML = '<option value="">Belt (optional)</option>' + Object.keys(belts).filter(b => !belts[b]).map(b => `<option>${b}</option>`).join('');
  c.value = ck;
}
async function refresh() {
  try { await Promise.all([loadFlights(), loadNotes(), loadHistory(), loadBelts()]); applyRole(); }
  catch (e) { flash('Cannot reach backend: ' + e.message, false); }
}
window.selectFlight = id => { selected = id; refresh(); };

// ---------- create (Builder) ----------
$('createBtn').onclick = async () => {
  try {
    const r = await api('POST', '/flights', {
      flightType: $('c_type').value, flightNumber: $('c_number').value, airline: $('c_airline').value,
      source: $('c_source').value, destination: $('c_dest').value, landingTime: $('c_time').value,
      gate: $('c_gate').value, baggageBelt: $('c_belt').value
    });
    show('Flight created via Builder + Factory Method', [r.flight.flightNumber + ' (' + r.flight.category + ')', r.gate, r.baggageService]);
    flash('Flight ' + r.flight.flightNumber + ' created', true);
    ['c_number', 'c_airline', 'c_source', 'c_dest', 'c_time', 'c_gate'].forEach(i => $(i).value = '');
    refresh();
  } catch (e) { flash(e.message, false); }
};

// ---------- actions ----------
const actions = {
  clear: async () => {
    const r = await api('POST', '/flights/' + selected + '/clear');
    show(r.approved ? 'Clearance APPROVED (Mediator -> Chain of Responsibility)' : 'Clearance REJECTED', r.steps);
    return r.approved ? 'Flight cleared' : 'Clearance rejected';
  },
  land: async () => {
    const f = await api('POST', '/flights/' + selected + '/land');
    show('Landed (Mediator: ATC -> Baggage Handling -> Control Center)',
      ['Status: ' + f.status, 'Belt: ' + (f.baggageBelt || '-'), 'Expected baggage: ' + (f.baggageArrivalSlot || '-')]);
    return 'Flight landed';
  },
  belt: async () => {
    const mode = $('a_strategy').value;
    const body = mode === 'MANUAL' ? { belt: $('a_belt').value } : { strategy: mode };
    const f = await api('PUT', '/flights/' + selected + '/baggage/belt', body);
    show('Belt assigned (' + (mode === 'MANUAL' ? 'manual' : 'Strategy: ' + mode) + ')', ['Belt: ' + f.baggageBelt]);
    return 'Belt ' + f.baggageBelt + ' assigned';
  },
  slot: async () => {
    const f = await api('PUT', '/flights/' + selected + '/baggage/slot', { slot: $('a_slot').value });
    show('Baggage slot set', ['Expected baggage: ' + f.baggageArrivalSlot]);
    return 'Slot set';
  },
  baggage: async () => {
    const r = await api('PUT', '/flights/' + selected + '/baggage/status',
      { baggageType: $('a_btype').value, handlingMethod: $('a_bhandle').value });
    show('Baggage processed (Template Method steps, handled through Bridge)', r.steps);
    return 'Baggage ' + r.baggage.baggageId + ' available';
  },
  clone: async () => {
    const f = await api('POST', '/flights/' + selected + '/clone',
      { flightNumber: $('k_number').value, destination: $('k_dest').value, landingTime: $('k_time').value });
    show('Flight cloned (Prototype)', [f.flightNumber + ' (' + f.flightId + ') created from the selected flight',
      'Status reset to ' + f.status + '; belt and slot not copied']);
    ['k_number', 'k_dest', 'k_time'].forEach(i => $(i).value = '');
    return 'Cloned as ' + f.flightNumber;
  },
  restore: async () => {
    const f = await api('POST', '/flights/' + selected + '/restore');
    show('State restored (Memento)', ['Status: ' + f.status, 'Belt: ' + (f.baggageBelt || '-'), 'Expected baggage: ' + (f.baggageArrivalSlot || '-')]);
    return 'Previous state restored';
  }
};
document.querySelectorAll('[data-act]').forEach(b => b.onclick = async () => {
  if (!selected) return flash('Select a flight first', false);
  try { flash(await actions[b.dataset.act](), true); refresh(); }
  catch (e) { flash(e.message, false); }
});

applyRole();
refresh();
setInterval(refresh, 3000);   // observers' output shows up without a manual reload
