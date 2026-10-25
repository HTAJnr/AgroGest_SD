/* AgroGest — app.js
 * Dropdowns, hambúrguer e comportamentos de UI.
 * Sem frameworks, sem módulos ES. Carregado com defer no topo.jspf.
 */
(function () {
  'use strict';

  /* ------------------------------------------------------------------ */
  /* Utilitários                                                          */
  /* ------------------------------------------------------------------ */

  /** Fecha todos os painéis abertos, excepto o indicado. */
  function fecharTodos(excepto) {
    document.querySelectorAll('[data-painel][aria-expanded="true"]').forEach(function (btn) {
      if (btn !== excepto) fecharDropdown(btn);
    });
  }

  function abrirDropdown(btn) {
    btn.setAttribute('aria-expanded', 'true');
    var painel = document.getElementById(btn.getAttribute('data-painel'));
    if (painel) {
      painel.removeAttribute('hidden');
      /* Foco no primeiro item focável */
      var primeiro = painel.querySelector('a, button');
      if (primeiro) primeiro.focus();
    }
  }

  function fecharDropdown(btn) {
    btn.setAttribute('aria-expanded', 'false');
    var painel = document.getElementById(btn.getAttribute('data-painel'));
    if (painel) painel.setAttribute('hidden', '');
  }

  /* ------------------------------------------------------------------ */
  /* Dropdowns (menu do utilizador e grupos de navegação admin)           */
  /* ------------------------------------------------------------------ */

  document.addEventListener('click', function (e) {
    var btn = e.target.closest('[data-painel]');

    if (btn) {
      e.stopPropagation();
      var aberto = btn.getAttribute('aria-expanded') === 'true';
      fecharTodos(btn);
      if (aberto) {
        fecharDropdown(btn);
      } else {
        abrirDropdown(btn);
      }
      return;
    }

    /* Clique fora: fechar tudo */
    fecharTodos(null);
  });

  /* Tecla Esc: fechar o dropdown mais próximo e devolver o foco */
  document.addEventListener('keydown', function (e) {
    if (e.key !== 'Escape') return;
    var aberto = document.querySelector('[data-painel][aria-expanded="true"]');
    if (!aberto) return;
    fecharDropdown(aberto);
    aberto.focus();
  });

  /* ------------------------------------------------------------------ */
  /* Hambúrguer (navbar admin em mobile)                                  */
  /* ------------------------------------------------------------------ */

  var hamburguer = document.getElementById('hamburguer');
  var navAdmin   = document.getElementById('nav-admin');

  if (hamburguer && navAdmin) {
    hamburguer.addEventListener('click', function () {
      var aberta = navAdmin.getAttribute('aria-expanded') === 'true';
      navAdmin.setAttribute('aria-expanded', aberta ? 'false' : 'true');
      hamburguer.setAttribute('aria-expanded', aberta ? 'false' : 'true');
      navAdmin.classList.toggle('nav-aberta', !aberta);
    });

    /* Esc fecha o menu hambúrguer */
    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape' && navAdmin.getAttribute('aria-expanded') === 'true') {
        navAdmin.setAttribute('aria-expanded', 'false');
        hamburguer.setAttribute('aria-expanded', 'false');
        navAdmin.classList.remove('nav-aberta');
        hamburguer.focus();
      }
    });
  }

})();
