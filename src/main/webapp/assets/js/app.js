/* AgroGest — app.js
 * Dropdowns, hambúrguer, toasts, feedback no campo, validação, confirmações.
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

  function atualizarHamburguer(aberta) {
    if (!hamburguer || !navAdmin) return;
    navAdmin.setAttribute('aria-expanded', aberta ? 'true' : 'false');
    hamburguer.setAttribute('aria-expanded', aberta ? 'true' : 'false');
    hamburguer.setAttribute('aria-label', aberta ? 'Fechar navegação' : 'Abrir navegação');
    navAdmin.classList.toggle('nav-aberta', aberta);
    var use = hamburguer.querySelector('use');
    if (use) {
      use.setAttribute('href', aberta ? '#i-fechar' : '#i-menu');
    }
  }

  if (hamburguer && navAdmin) {
    hamburguer.addEventListener('click', function () {
      var aberta = navAdmin.getAttribute('aria-expanded') === 'true';
      atualizarHamburguer(!aberta);
    });

    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape' && navAdmin.getAttribute('aria-expanded') === 'true') {
        atualizarHamburguer(false);
        hamburguer.focus();
      }
    });
  }

  /* ------------------------------------------------------------------ */
  /* Toasts                                                               */
  /* ------------------------------------------------------------------ */

  var toastsContainer = document.getElementById('toasts-container');
  var estaPaginaAuth  = document.body.classList.contains('pagina-auth');

  /**
   * Cria e mostra um toast.
   * @param {string} msg      - Texto da mensagem.
   * @param {'ok'|'erro'} tipo
   */
  function mostrarToast(msg, tipo) {
    if (!toastsContainer) return;

    var toast = document.createElement('div');
    toast.className = 'toast toast-' + tipo;
    toast.setAttribute('role', tipo === 'erro' ? 'alert' : 'status');

    /* Ícone */
    var svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('class', 'icone');
    svg.setAttribute('aria-hidden', 'true');
    var use = document.createElementNS('http://www.w3.org/2000/svg', 'use');
    use.setAttribute('href', tipo === 'ok' ? '#i-check' : '#i-alerta');
    svg.appendChild(use);

    /* Texto */
    var span = document.createElement('span');
    span.className = 'toast-msg';
    span.textContent = msg;

    toast.appendChild(svg);
    toast.appendChild(span);

    /* Botão fechar (sempre visível para erros; para ok aparece por CSS) */
    var btnFechar = document.createElement('button');
    btnFechar.className = 'toast-fechar btn-user';
    btnFechar.setAttribute('aria-label', 'Fechar');
    btnFechar.setAttribute('title', 'Fechar');
    var svgX = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svgX.setAttribute('class', 'icone');
    svgX.setAttribute('aria-hidden', 'true');
    var useX = document.createElementNS('http://www.w3.org/2000/svg', 'use');
    useX.setAttribute('href', '#i-fechar');
    svgX.appendChild(useX);
    btnFechar.appendChild(svgX);
    toast.appendChild(btnFechar);

    toastsContainer.appendChild(toast);

    /* Limitar a 3 toasts */
    var todos = toastsContainer.querySelectorAll('.toast');
    if (todos.length > 3) {
      todos[0].remove();
    }

    /* Fechar ao clicar */
    btnFechar.addEventListener('click', function () { removerToast(toast); });

    /* ok: auto-fechar em 5 s, pausar em hover/foco */
    if (tipo === 'ok') {
      var timer = setTimeout(function () { removerToast(toast); }, 5000);
      toast.addEventListener('mouseenter', function () { clearTimeout(timer); });
      toast.addEventListener('mouseleave', function () {
        timer = setTimeout(function () { removerToast(toast); }, 2000);
      });
      toast.addEventListener('focusin',  function () { clearTimeout(timer); });
      toast.addEventListener('focusout', function () {
        timer = setTimeout(function () { removerToast(toast); }, 2000);
      });
    }
  }

  function removerToast(toast) {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(110%)';
    toast.style.transition = 'opacity 0.18s, transform 0.18s';
    setTimeout(function () { toast.remove(); }, 200);
  }

  /**
   * Converte os .alerta existentes no DOM em toasts, exceto na página de auth
   * onde ficam inline no cartão.
   */
  function converterAlertasEmToasts() {
    if (estaPaginaAuth || !toastsContainer) return;

    var avisos = document.querySelector('.avisos');
    if (!avisos) return;

    avisos.querySelectorAll('.alerta').forEach(function (el) {
      var tipo = el.classList.contains('ok') ? 'ok' : 'erro';
      var msg  = el.textContent.trim();
      if (msg) mostrarToast(msg, tipo);
      el.remove();
    });

    /* Esconder o .avisos se ficou vazio */
    if (!avisos.querySelector('.alerta')) {
      avisos.style.display = 'none';
    }
  }

  /* ------------------------------------------------------------------ */
  /* Feedback no elemento (servidor)                                      */
  /* ------------------------------------------------------------------ */

  /**
   * Marca o campo com erro, insere .campo-erro abaixo e faz focus.
   * @param {HTMLFormElement} form
   * @param {string}          nomeCampo  - Valor do atributo name do input.
   * @param {string}          msg        - Mensagem de erro.
   */
  function marcarCampoErro(form, nomeCampo, msg) {
    if (!nomeCampo || !form) return;

    /* Tentar localizar o campo pelo name */
    var campo = form.querySelector('[name="' + CSS.escape(nomeCampo) + '"]');
    if (!campo) return;

    /* Para radio/select marcar o contentor .campo; para outros, o próprio input */
    var alvo = campo.closest('.campo') || campo;

    /* Remover erro anterior no mesmo campo */
    var erroAnterior = alvo.querySelector('.campo-erro');
    if (erroAnterior) erroAnterior.remove();

    campo.setAttribute('aria-invalid', 'true');
    campo.classList.add('invalido');

    var erroId = 'ce-' + nomeCampo + '-' + Date.now();
    campo.setAttribute('aria-describedby', erroId);

    var small = document.createElement('small');
    small.className = 'campo-erro';
    small.id = erroId;

    var svgAlerta = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svgAlerta.setAttribute('class', 'icone');
    svgAlerta.setAttribute('aria-hidden', 'true');
    var useAlerta = document.createElementNS('http://www.w3.org/2000/svg', 'use');
    useAlerta.setAttribute('href', '#i-alerta');
    svgAlerta.appendChild(useAlerta);

    small.appendChild(svgAlerta);
    small.appendChild(document.createTextNode(' ' + msg));

    /* Inserir depois do campo (ou do contentor) */
    if (campo.closest('.campo')) {
      campo.closest('.campo').appendChild(small);
    } else {
      campo.insertAdjacentElement('afterend', small);
    }

    /* Foco e scroll */
    campo.focus();
    campo.scrollIntoView({ block: 'center', behavior: 'smooth' });
  }

  /**
   * Aplica o feedback de campo do servidor assim que a página carrega.
   * Lê o data-campo do .avisos e a mensagem do primeiro .alerta.erro.
   */
  function aplicarFeedbackServidor() {
    if (estaPaginaAuth || !toastsContainer) return;

    var avisos = document.querySelector('.avisos');
    if (!avisos) return;

    var nomeCampo = (avisos.getAttribute('data-campo') || '').trim();
    var alertaErro = avisos.querySelector('.alerta.erro');
    if (!nomeCampo || !alertaErro) return;

    /* Localizar o formulário da página */
    var form = document.querySelector('form[data-validar]') || document.querySelector('form[method="post"]');
    if (!form) return;

    var msg = alertaErro.textContent.trim();
    marcarCampoErro(form, nomeCampo, msg);
  }

  /* ------------------------------------------------------------------ */
  /* Limpar erros de campo ao digitar                                     */
  /* ------------------------------------------------------------------ */

  document.addEventListener('input', function (e) {
    var campo = e.target;
    if (campo.classList.contains('invalido') && campo.validity && campo.validity.valid) {
      campo.classList.remove('invalido');
      campo.removeAttribute('aria-invalid');
      campo.removeAttribute('aria-describedby');
      var erroEl = campo.closest('.campo') && campo.closest('.campo').querySelector('.campo-erro');
      if (!erroEl) erroEl = campo.nextElementSibling && campo.nextElementSibling.classList.contains('campo-erro') ? campo.nextElementSibling : null;
      if (erroEl) erroEl.remove();
    }
  }, true);

  /* ------------------------------------------------------------------ */
  /* Validação no cliente (form[data-validar])                            */
  /* ------------------------------------------------------------------ */

  /* Mensagens em português */
  function msgValidacao(campo) {
    var v = campo.validity;
    if (v.valueMissing)  return 'Preencha este campo.';
    if (v.typeMismatch && campo.type === 'email') return 'Introduza um email válido.';
    if (v.patternMismatch) {
      /* Detectar se é o padrão do telefone moçambicano */
      if (campo.getAttribute('inputmode') === 'tel' || campo.name === 'telefone' || campo.name === 'contacto') {
        return 'Introduza um número moçambicano válido, por exemplo 84 123 4567.';
      }
      return 'Formato inválido.';
    }
    if (v.tooShort) return 'Mínimo ' + campo.minLength + ' caracteres.';
    if (v.tooLong)  return 'Máximo ' + campo.maxLength + ' caracteres.';
    if (v.rangeUnderflow) return 'O valor tem de ser maior que ' + campo.min + '.';
    if (v.rangeOverflow)  return 'O valor tem de ser menor que ' + campo.max + '.';
    if (v.stepMismatch)   return 'Valor inválido para o passo ' + campo.step + '.';
    return campo.validationMessage || 'Valor inválido.';
  }

  function mostrarErroCliente(campo, msg) {
    campo.setAttribute('aria-invalid', 'true');
    campo.classList.add('invalido');

    /* Remover erro anterior */
    var contentor = campo.closest('.campo');
    var erroExist = contentor ? contentor.querySelector('.campo-erro') : campo.nextElementSibling;
    if (erroExist && erroExist.classList && erroExist.classList.contains('campo-erro')) erroExist.remove();

    var erroId = 'ce-' + (campo.name || campo.id || Math.random().toString(36).slice(2));
    campo.setAttribute('aria-describedby', erroId);

    var small = document.createElement('small');
    small.className = 'campo-erro';
    small.id = erroId;

    var svgA = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svgA.setAttribute('class', 'icone');
    svgA.setAttribute('aria-hidden', 'true');
    var useA = document.createElementNS('http://www.w3.org/2000/svg', 'use');
    useA.setAttribute('href', '#i-alerta');
    svgA.appendChild(useA);
    small.appendChild(svgA);
    small.appendChild(document.createTextNode(' ' + msg));

    if (contentor) {
      contentor.appendChild(small);
    } else {
      campo.insertAdjacentElement('afterend', small);
    }
  }

  function limparErroCliente(campo) {
    campo.classList.remove('invalido');
    campo.removeAttribute('aria-invalid');
    campo.removeAttribute('aria-describedby');
    var contentor = campo.closest('.campo');
    var erroExist = contentor ? contentor.querySelector('.campo-erro') : campo.nextElementSibling;
    if (erroExist && erroExist.classList && erroExist.classList.contains('campo-erro')) erroExist.remove();
  }

  function validarFormulario(form) {
    var campos = form.querySelectorAll('input:not([disabled]):not([type="hidden"]):not([type="submit"]):not([type="button"]), select:not([disabled]), textarea:not([disabled])');
    var primeiroCampoInvalido = null;

    campos.forEach(function (c) {
      if (!c.checkValidity()) {
        mostrarErroCliente(c, msgValidacao(c));
        if (!primeiroCampoInvalido) primeiroCampoInvalido = c;
      } else {
        limparErroCliente(c);
      }
    });

    if (primeiroCampoInvalido) {
      primeiroCampoInvalido.focus();
      primeiroCampoInvalido.scrollIntoView({ block: 'center', behavior: 'smooth' });
      return false;
    }
    return true;
  }

  /* Validação em blur */
  document.addEventListener('blur', function (e) {
    var campo = e.target;
    var form  = campo.form || campo.closest('form');
    if (!form || !form.hasAttribute('data-validar')) return;
    if (campo.type === 'hidden' || campo.type === 'submit' || campo.type === 'button') return;
    if (!campo.checkValidity()) {
      mostrarErroCliente(campo, msgValidacao(campo));
    } else {
      limparErroCliente(campo);
    }
  }, true);

  /* Validação no submit */
  document.addEventListener('submit', function (e) {
    var form = e.target;
    if (!form.hasAttribute('data-validar')) return;
    if (!validarFormulario(form)) {
      e.preventDefault();
      return;
    }
    /* Duplo clique: desactivar o botão de submit */
    var btnSubmit = form.querySelector('[type="submit"]');
    if (btnSubmit && !btnSubmit.disabled) {
      var textoOriginal = btnSubmit.textContent;
      btnSubmit.disabled = true;
      btnSubmit.textContent = 'A guardar…';
      /* Repor após 8 s (caso o servidor devolva um erro de validação) */
      setTimeout(function () {
        btnSubmit.disabled = false;
        btnSubmit.textContent = textoOriginal;
      }, 8000);
    }
  });

  /* ------------------------------------------------------------------ */
  /* Duplo clique em qualquer form POST (complementa V2)                  */
  /* Nota: só desactiva; a validação acima cuida dos form[data-validar]. */
  /* ------------------------------------------------------------------ */

  document.addEventListener('submit', function (e) {
    var form = e.target;
    if (form.hasAttribute('data-validar')) return; /* já tratado acima */
    var btnSubmit = form.querySelector('[type="submit"]:not([disabled])');
    if (!btnSubmit) return;
    /* Esperar um frame para não bloquear antes do form ser enviado */
    var textoOriginal = btnSubmit.innerHTML;
    setTimeout(function () {
      if (!form.hasAttribute('data-validar')) {
        btnSubmit.disabled = true;
      }
    }, 0);
    /* Repor após 8 s */
    setTimeout(function () {
      btnSubmit.disabled = false;
      btnSubmit.innerHTML = textoOriginal;
    }, 8000);
  });

  /* ------------------------------------------------------------------ */
  /* Confirmações via <dialog> nativo (data-confirm="mensagem")           */
  /* ------------------------------------------------------------------ */

  var dlg       = document.getElementById('dlg-confirm');
  var dlgMsg    = document.getElementById('dlg-confirm-msg');
  var dlgCancel = document.getElementById('dlg-cancel');
  var dlgOk     = document.getElementById('dlg-ok');

  /* Callback a executar quando o utilizador confirmar */
  var acaoConfirmada = null;

  if (dlg && dlgCancel && dlgOk) {
    dlgCancel.addEventListener('click', function () {
      dlg.close();
      acaoConfirmada = null;
    });

    dlgOk.addEventListener('click', function () {
      dlg.close();
      if (typeof acaoConfirmada === 'function') {
        acaoConfirmada();
        acaoConfirmada = null;
      }
    });

    /* Fechar com Esc (comportamento nativo do dialog, mas garantimos o reset) */
    dlg.addEventListener('cancel', function () {
      acaoConfirmada = null;
    });
  }

  /**
   * Abre o dialog de confirmação.
   * @param {string}   msg - Mensagem a mostrar.
   * @param {Function} ok  - Função chamada ao confirmar.
   * @param {boolean}  perigo - Se true, o botão OK fica com btn-perigo.
   */
  function confirmar(msg, ok, perigo) {
    if (!dlg) {
      /* Fallback sem dialog: confirm nativo */
      if (window.confirm(msg)) ok();
      return;
    }
    if (dlgMsg) dlgMsg.textContent = msg;
    if (dlgOk) {
      dlgOk.className = 'btn' + (perigo ? ' btn-perigo' : '');
    }
    acaoConfirmada = ok;
    dlg.showModal();
  }

  /* Delegar cliques em botões com data-confirm */
  document.addEventListener('click', function (e) {
    /* Evitar que o dialog em si dispare esta lógica */
    if (e.target.closest('#dlg-confirm')) return;

    var btn = e.target.closest('[data-confirm]');
    if (!btn) return;

    e.preventDefault();
    e.stopPropagation();

    var msg    = btn.getAttribute('data-confirm') || 'Tem a certeza?';
    var perigo = btn.classList.contains('btn-perigo');

    confirmar(msg, function () {
      /* Simular o clique original sem o handler de confirmação */
      btn.removeAttribute('data-confirm');
      btn.click();
      btn.setAttribute('data-confirm', msg);
    }, perigo);
  });

  /* ------------------------------------------------------------------ */
  /* Fotos com erro → placeholder                                         */
  /* ------------------------------------------------------------------ */

  document.querySelectorAll('img.produto-foto, img.produto-mini').forEach(function (img) {
    function aplicarPlaceholder() {
      /* Evitar loop infinito se o placeholder também falhar */
      if (img.src.indexOf('produto-placeholder') === -1) {
        var placeholder = img.getAttribute('data-placeholder');
        if (!placeholder) {
          var idx = img.src.indexOf('/assets/img/');
          if (idx !== -1) {
            placeholder = img.src.substring(0, idx) + '/assets/img/produto-placeholder.svg';
          } else {
            placeholder = '/assets/img/produto-placeholder.svg';
          }
        }
        img.src = placeholder;
      }
    }

    img.addEventListener('error', aplicarPlaceholder);

    /* Se a imagem já falhou antes de registar o listener */
    if (img.complete && img.naturalWidth === 0 && img.src) {
      aplicarPlaceholder();
    }
  });

  /* ------------------------------------------------------------------ */
  /* Inicialização                                                        */
  /* ------------------------------------------------------------------ */

  /* 1. Primeiro feedback de campo (vem do servidor) */
  aplicarFeedbackServidor();

  /* 2. Converter alertas em toasts */
  converterAlertasEmToasts();

})();
