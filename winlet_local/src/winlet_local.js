String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

(function( jQuery, window, undefined ) {
	"use strict";
	 
	var matched, browser;
	 
	jQuery.uaMatch = function( ua ) {
	  ua = ua.toLowerCase();
	 
		var match = /(chrome)[ \/]([\w.]+)/.exec( ua ) ||
			/(webkit)[ \/]([\w.]+)/.exec( ua ) ||
			/(opera)(?:.*version|)[ \/]([\w.]+)/.exec( ua ) ||
			/(msie) ([\w.]+)/.exec( ua ) ||
			ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec( ua ) ||
			[];

		var platform_match = /(ipad)/.exec( ua ) ||
			/(iphone)/.exec( ua ) ||
			/(android)/.exec( ua ) ||
			[];
	 
		return {
			browser: match[ 1 ] || "",
			version: match[ 2 ] || "0",
			platform: platform_match[0] || ""
		};
	};
	 
	matched = jQuery.uaMatch( window.navigator.userAgent );
	browser = {};
	 
	if ( matched.browser ) {
		browser[ matched.browser ] = true;
		browser.version = matched.version;
	}


	if ( matched.platform) {
		browser[ matched.platform ] = true
	}
	 
	// Chrome is Webkit, but Webkit is also Safari.
	if ( browser.chrome ) {
		browser.webkit = true;
	} else if ( browser.webkit ) {
		browser.safari = true;
	}
	 
	jQuery.browser = browser;
	 
	})( jQuery, window );

/*
 * jQuery hashchange event - v1.3 - 7/21/2010
 * http://benalman.com/projects/jquery-hashchange-plugin/
 * 
 * Copyright (c) 2010 "Cowboy" Ben Alman
 * Dual licensed under the MIT and GPL licenses.
 * http://benalman.com/about/license/
 */
(function($,e,b){var c="hashchange",h=document,f,g=$.event.special,i=h.documentMode,d="on"+c in e&&(i===b||i>7);function a(j){j=j||location.href;return"#"+j.replace(/^[^#]*#?(.*)$/,"$1")}$.fn[c]=function(j){return j?this.bind(c,j):this.trigger(c)};$.fn[c].delay=50;g[c]=$.extend(g[c],{setup:function(){if(d){return false}$(f.start)},teardown:function(){if(d){return false}$(f.stop)}});f=(function(){var j={},p,m=a(),k=function(q){return q},l=k,o=k;j.start=function(){p||n()};j.stop=function(){p&&clearTimeout(p);p=b};function n(){var r=a(),q=o(m);if(r!==m){l(m=r,q);$(e).trigger(c)}else{if(q!==m){location.href=location.href.replace(/#.*/,"")+q}}p=setTimeout(n,$.fn[c].delay)}$.browser.msie&&!d&&(function(){var q,r;j.start=function(){if(!q){r=$.fn[c].src;r=r&&r+a();q=$('<iframe tabindex="-1" title="empty"/>').hide().one("load",function(){r||l(a());n()}).attr("src",r||"javascript:0").insertAfter("body")[0].contentWindow;h.onpropertychange=function(){try{if(event.propertyName==="title"){q.document.title=h.title}}catch(s){}}}};j.stop=k;o=function(){return a(q.location.href)};l=function(v,s){var u=q.document,t=$.fn[c].domain;if(v!==s){u.title=h.title;u.open();t&&u.write('<script>document.domain="'+t+'"<\/script>');u.close();q.location.hash=v}}})();return j})()})(jQuery,this);

/*
 * jQuery BBQ: Back Button & Query Library - v1.3pre - 8/26/2010
 * http://benalman.com/projects/jquery-bbq-plugin/
 * 
 * Copyright (c) 2010 "Cowboy" Ben Alman
 * Dual licensed under the MIT and GPL licenses.
 * http://benalman.com/about/license/
 */

(function($,r){var h,n=Array.prototype.slice,t=decodeURIComponent,a=$.param,j,c,m,y,b=$.bbq=$.bbq||{},s,x,k,e=$.event.special,d="hashchange",B="querystring",F="fragment",z="elemUrlAttr",l="href",w="src",p=/^.*\?|#.*$/g,u,H,g,i,C,E={};function G(I){return typeof I==="string"}function D(J){var I=n.call(arguments,1);return function(){return J.apply(this,I.concat(n.call(arguments)))}}function o(I){return I.replace(H,"$2")}function q(I){return I.replace(/(?:^[^?#]*\?([^#]*).*$)?.*/,"$1")}function f(K,P,I,L,J){var R,O,N,Q,M;if(L!==h){N=I.match(K?H:/^([^#?]*)\??([^#]*)(#?.*)/);M=N[3]||"";if(J===2&&G(L)){O=L.replace(K?u:p,"")}else{Q=m(N[2]);L=G(L)?m[K?F:B](L):L;O=J===2?L:J===1?$.extend({},L,Q):$.extend({},Q,L);O=j(O);if(K){O=O.replace(g,t)}}R=N[1]+(K?C:O||!N[1]?"?":"")+O+M}else{R=P(I!==h?I:location.href)}return R}a[B]=D(f,0,q);a[F]=c=D(f,1,o);a.sorted=j=function(J,K){var I=[],L={};$.each(a(J,K).split("&"),function(P,M){var O=M.replace(/(?:%5B|=).*$/,""),N=L[O];if(!N){N=L[O]=[];I.push(O)}N.push(M)});return $.map(I.sort(),function(M){return L[M]}).join("&")};c.noEscape=function(J){J=J||"";var I=$.map(J.split(""),encodeURIComponent);g=new RegExp(I.join("|"),"g")};c.noEscape(",/");c.ajaxCrawlable=function(I){if(I!==h){if(I){u=/^.*(?:#!|#)/;H=/^([^#]*)(?:#!|#)?(.*)$/;C="#!"}else{u=/^.*#/;H=/^([^#]*)#?(.*)$/;C="#"}i=!!I}return i};c.ajaxCrawlable(0);$.deparam=m=function(L,I){var K={},J={"true":!0,"false":!1,"null":null};$.each(L.replace(/\+/g," ").split("&"),function(O,T){var N=T.split("="),S=t(N[0]),M,R=K,P=0,U=S.split("]["),Q=U.length-1;if(/\[/.test(U[0])&&/\]$/.test(U[Q])){U[Q]=U[Q].replace(/\]$/,"");U=U.shift().split("[").concat(U);Q=U.length-1}else{Q=0}if(N.length===2){M=t(N[1]);if(I){M=M&&!isNaN(M)?+M:M==="undefined"?h:J[M]!==h?J[M]:M}if(Q){for(;P<=Q;P++){S=U[P]===""?R.length:U[P];R=R[S]=P<Q?R[S]||(U[P+1]&&isNaN(U[P+1])?{}:[]):M}}else{if($.isArray(K[S])){K[S].push(M)}else{if(K[S]!==h){K[S]=[K[S],M]}else{K[S]=M}}}}else{if(S){K[S]=I?h:""}}});return K};function A(K,I,J){if(I===h||typeof I==="boolean"){J=I;I=a[K?F:B]()}else{I=G(I)?I.replace(K?u:p,""):I}return m(I,J)}m[B]=D(A,0);m[F]=y=D(A,1);$[z]||($[z]=function(I){return $.extend(E,I)})({a:l,base:l,iframe:w,img:w,input:w,form:"action",link:l,script:w});k=$[z];function v(L,J,K,I){if(!G(K)&&typeof K!=="object"){I=K;K=J;J=h}return this.each(function(){var O=$(this),M=J||k()[(this.nodeName||"").toLowerCase()]||"",N=M&&O.attr(M)||"";O.attr(M,a[L](N,K,I))})}$.fn[B]=D(v,B);$.fn[F]=D(v,F);b.pushState=s=function(L,I){if(G(L)&&/^#/.test(L)&&I===h){I=2}var K=L!==h,J=c(location.href,K?L:{},K?I:2);location.href=J};b.getState=x=function(I,J){return I===h||typeof I==="boolean"?y(I):y(J)[I]};b.removeState=function(I){var J={};if(I!==h){J=x();$.each($.isArray(I)?I:arguments,function(L,K){delete J[K]})}s(J,2)};e[d]=$.extend(e[d],{add:function(I){var K;function J(M){var L=M[F]=c();M.getState=function(N,O){return N===h||typeof N==="boolean"?m(L,N):m(L,O)[N]};K.apply(this,arguments)}if($.isFunction(I)){K=I;return J}else{K=I.handler;I.handler=J}}})})(jQuery,this);

function ElmRect(elm) {
	if (elm == null)
		return null;

	var posi = elm.offset();
	this.left = posi.left;
	this.top = posi.top;
	this.width = elm[0].offsetWidth;
	this.height = elm[0].offsetHeight;
	this.right = this.left + this.width;
	this.bottom = this.top + this.height;
}

jQuery.fn.winform = function() {
	var settings = $.extend({}, arguments[0]);

	return this.each(function() {
		if (this.winsubmit)
			return;

		// 给表单加上提交处理方法
		this.winsubmit = function() {
			if (this.onsubmit != null)
				try {
					if (!this.onsubmit())
						return false;
				} catch (e) {
					alert(e);
					return false;
				}

			if ($(this).attr("enctype") == "multipart/form-data") {
				this.action = this.action + "&" + AeJSEngine.mergeParam(settings.wid, {_pg: window.location.pathname, _purl: window.location.href});
				return true;
			}

			if ($(this).attr('method').toUpperCase() != 'POST') {
				// GET的处理。方式无需调用action，直接刷新window
				var params = $.deparam($(this).serialize());
				$(this).find(":checkbox").each(function() {
					if (params[this.name] == undefined)
						params[this.name] = "";
				});
				AeJSEngine.setHash(settings.wid, params);
				AeJSEngine.loadContent(settings.wid);
				AeJSEngine.updateWindows(settings.wid, settings.update);
				
				return false;
			}

			// POST的处理
			try {
				if (settings.hideloading != 'yes') {
					AeJSEngine.showLoading('#ap_win_' + settings.wid, settings.dialog);
				}
			} catch (e) {
			}

			var disabled = new Array();
			var inputs = $(this).find(":input").find(":disabled");
			for (var i = 0; i < inputs.length; i++)
				if (inputs[i].name != '')
					disabled[disabled.length] = inputs[i].name;

			var fields = new Array();
			inputs = $(this).find(":input");
			for (var i = 0; i < inputs.length; i++)
				if (inputs[i].name != '')
					fields[fields.length] = inputs[i].name;

			$.ajax({
				  type: 'POST',
				  url: this.action,
				  data: AeJSEngine.mergeParam(settings.wid, $.deparam($(this).serialize()), {_x: 'y', _v: settings.validate, _ff: fields, _fd: disabled, _pg: window.location.pathname, _purl: window.location.href}),
				  success: AeJSEngine.getActionResponseHandler(settings.wid, this, this.aftersubmit),
				  error: AeJSEngine.getErrorHandler(settings.wid),
				  dataType: "text"
				});

			return false;
		};

		this.ajaxValidate = function(input, name, value) {
			if (name == undefined)
				name = input.name;

			AeJSEngine.form.validating(input);

			var val = value;
			
			if (input.type == 'checkbox') {
				if (input.checked)
					val = input.value;
			} else
				val = input.value;

			var param = {_vf: name, _vv: val, _vid: input.id};
			param[name] = val;

			$.ajax({
				  type: 'POST',
				  url: this.action,
				  data: AeJSEngine.mergeParam(settings.wid, param, {_x: 'y', _pg: window.location.pathname, _purl: window.location.href}),
				  success: AeJSEngine.form.getValidateResponseHandler(input.form, name, input),
				  dataType: "json"
				});
		};

		$(this).submit(this.winsubmit);

		if (settings.focus) {
			var inp = $(this).find('input[name="' + settings.focus + '"]');
			inp.select();
			inp.focus();
		}

		// 如果表单内动态增减了输入字段，需要调用这个方法来更新ajax数据校验机制
		this.updateValidate = function() {
			if (settings.validate == 'yes') {
				var form = this;
				var $form = $(form);

				$form.find(":input").each(function() {
					if (this.name == null || this.name == '')
						return;

					var $this = $(this);
					if ($this.data("validate") == "no")
						return;

					if ($this.prop('tagName') == 'INPUT') {
						var type = $(this).prop("type");
						if (!(type == "text" || type == "email" || type == "checkbox" || type == "radio" || type == "password")) {
							return;
						}
					}

					if ($this.attr("tips") != undefined) {
						$this.off("blur").off("fucos").focus(function(){
							AeJSEngine.form.showTip(this);
						}).blur(function(){
							AeJSEngine.form.hideTip(this);
						});
					}

					$this.off('change').change(function(){
						this.form.ajaxValidate(this);
					});
				});
			}
		};

		this.updateValidate();
	});
};

var AeJSEngine = {
	ImgBg: new Image(1,1),
	ImgLoading: new Image(1,1),
	ImgValidating: new Image(1,1),

	topSpace: 0,
	bottomSpace: 0,
	leftSpace: 0,
	rightSpace: 0,

	widCounter: 0,
	isStatic: false,
	isApme: false,
	detectHashChange: true,

	reEscape: /(:|\.|\[|\])/g,
	reScriptAll: new RegExp('<script.*?>(?:\n|\r|.)*?<\/script>', 'img'),
	reScriptOne: new RegExp('<script(.*?)>((?:\n|\r|.)*?)<\/script>', 'im'),
	reScriptLanguage: new RegExp('.*?language.*?=.*?"(.*?)"', 'im'),
	reScriptSrc: new RegExp('.*?src.*?=.*?"(.*?)"', 'im'),
	reScriptType: new RegExp('.*?type.*?=.*?"(.*?)"', 'im'),
	reScriptCharset: new RegExp('.*?charset.*?=.*?"(.*?)"', 'im'),
	reCSSAll: new RegExp('<link.*?type="text/css".*?>', 'img'),
	reCSSHref: new RegExp('.*?href="(.*?)"', 'im'),
	reWinWid: new RegExp('win\\$\\.wid\\s*\\(.*\\)', 'img'),
	reWinPost: new RegExp('win\\$\\.post\\s*\\(', 'img'),
	reWinAjax: new RegExp('win\\$\\.ajax\\s*\\(', 'img'),
	reWinGet: new RegExp('win\\$\\.get\\s*\\(', 'img'),
	reWinToggle: new RegExp('win\\$\\.toggle\\s*\\(', 'img'),
	reWinUrl: new RegExp('win\\$\\.url\\s*\\(', 'img'),
	reWinSubmit: new RegExp('win\\$\\.submit\\s*\\(', 'img'),
	reWinAfterSubmit: new RegExp('win\\$\\.aftersubmit\\s*\\(', 'img'),
	reWinlet: new RegExp('^((\\w+/.+)/(\\w+))(\\?([^\\s]+))?(\\s+(.*?))?$'),
	reWinletParam: new RegExp('(\\w+)\\:(\\w+)'),
	reDialogSetting: new RegExp('<div id="ap_dialog">(.*?)<\/div>', 'img'),

	hashGroupsByUri: {},
	hashGroupByRootWindow: {},

	form: {
		createResultHolder: function(input) {
			if (input.m_result != null)
				return input.m_result;
			if ($(input).data("validate") == "no")
				return null;
			input.m_result = $(input.form).find("span.validate_result[data-input='" + input.name.replace("[", "\\[").replace("]", "\\]").replace(".", "\\.") + "']");
			if (input.m_result.length == 0) {
				input.m_result = $('<span class="validate_result"></span>');
				$(input).after(input.m_result);
			}
		},

		/**
		 * 返回的是jQuery对象
		 * 用AeJSEngine.form不用this是为了重载
		 * 
		 * input: input元素对象
		 */
		getInputResult: function(input) {
			if (input.m_result == null)
				AeJSEngine.form.createResultHolder(input);

			return input.m_result;
		},

		validateClearAll: function(form) {
			$(form).find("span.validate_result").each(function() {
				$(this).html('');
			});
		},

		validateClear: function(input) {
			var result = AeJSEngine.form.getInputResult(input);
			if (result != null)
				result.html('');
		},

		validating: function(input) {
		},

		validateSuccess: function(input) {
			var result = AeJSEngine.form.getInputResult(input);
			if (result != null)
				result.html('<span class="win_valpassed">&nbsp;</span>');
		},

		validateError: function(input, msg) {
			var result = AeJSEngine.form.getInputResult(input);

			if (result == null)
				return;

			var failed = result.find("div.win_valfailed");
			if (failed.length == 0) {
				result.html("<div class='win_valfailed'></div>");
				failed = result.find("div.win_valfailed");
			}

			failed.text(msg);
		},

		applyChanges: function(json, form, input) {
			var changes = null;
			try {
				changes = eval(json);
			} catch (e) {
			}
			
			if (changes != null) {
				for (var i = 0; i < changes.length; i++) {
					var inp = $(form).find(":input[name='" + changes[i].input + "']");

					if (inp.length == 0)
						inp = $("#" + changes[i].input);

					if (inp.length == 0)
						continue;

					inp = inp[0];

					if (changes[i].type == 'v') { // 校验结果
						if (changes[i].message != '') {
							AeJSEngine.form.validateClear(inp);
							AeJSEngine.form.validateError(inp, changes[i].message);
						}
					} else {
						if (changes[i].type == 'u') { // 更新值
							if (inp.type == 'radio')
								$(form).children(":input[name='" + changes[i].input + "'][value='" + changes[i].value + "']").attr('checked', 'checked');
							else if (inp.type == 'checkbox')
								inp.checked = changes[i].value;
							else {
								if (input == inp)
									AeJSEngine.form.validateSuccess(input);
								$(inp).val(changes[i].value);
							}
						} else if (changes[i].type == 'd') {
							inp.disabled = true;
							AeJSEngine.form.validateClear(inp);
						} else if (changes[i].type == 'e') {
							inp.disabled = false;
						} else if (changes[i].type == 'l') { // 更新列表
							if (inp.type = 'select') {
								$(inp).empty();
								for (var j = 0; j < changes[i].list.length; j++)
									$(inp).append('<option value="' + changes[i].list[j].id + '">' + changes[i].list[j].name + '</option>');
							}
						}
					}
				}
			}
		},

		getValidateResponseHandler: function(form, name, input) {
			return function(json) {
				AeJSEngine.form.validateClear(input);

				AeJSEngine.form.applyChanges(json, form, input);
				
				if (form.onerror != undefined && input != undefined) {
					try {
						form.onerror(input);
					} catch (e) {
					}
				}
			};
		},
		
		showTip: function(input) {
			var result = AeJSEngine.form.getInputResult(input);
			if (result != null && result.html() == '')
				result.html('<div class="win_tips">' + $(this).attr('tips') + '</div>');
		},

		hideTip: function(input) {
			var result = AeJSEngine.form.getInputResult(input);
			if (result != null && result.find("div.win_tips").length > 0)
				result.html('');
		}
	},

	_utf8_decode: function(utftext){
		if (utftext == null)
			return null;

		utftext = unescape(utftext);

        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;
        
        while (i < utftext.length) {
        
            c = utftext.charCodeAt(i);
            
            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            }
            else 
                if ((c > 191) && (c < 224)) {
                    c2 = utftext.charCodeAt(i + 1);
                    string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                    i += 2;
                }
                else {
                    c2 = utftext.charCodeAt(i + 1);
                    c3 = utftext.charCodeAt(i + 2);
                    string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                    i += 3;
                }
        }
        return string;
    },

    getRootWinId: function(wid) {
    	wid = wid.toString();
    	return wid.substr(0, 1 + parseInt(wid.substr(0, 1)));
    },

    isRootWinId: function(wid) {
    	return AeJSEngine.getRootWinId(wid) == wid;
    },

	getWinSettings: function(wid) {
		return $('#ap_win_' + AeJSEngine.getRootWinId(wid))[0].settings;
	},

	getHash: function(wid) {
		var hashgroup = AeJSEngine.hashGroupByRootWindow[AeJSEngine.getRootWinId(wid)].idx;

		try {
			return $.param($.deparam(window.location.toString().split('#')[1])[hashgroup], true);
		} catch (e){
		}
		return "";
	},

	setHash: function(wid, hash, toggle) {
		var hashgroup = AeJSEngine.hashGroupByRootWindow[AeJSEngine.getRootWinId(wid)].idx;
		var params = null;

		try {
			params = $.deparam(window.location.toString().split('#')[1]);
		} catch (e){
		}

		if (!(params instanceof Object))
			params = {};
		if (params[hashgroup] == undefined)
			params[hashgroup] = {};

		if (toggle) {
			for (property in hash) {
				if ($.isArray(hash[property])) { // array property
					if (!($.isArray(params[hashgroup][property]))) { // not array in hash, replace directly
						params[hashgroup][property] = hash[property];
					} else { // array in hash as well
						$.each(hash[property], function(index, value){
							value = "" + value;
							var idx = $.inArray(value, params[hashgroup][property]);
							if (idx < 0) // value not in hash array, add
								params[hashgroup][property].push(value);
							else // value already in hash array, remove
								params[hashgroup][property].splice(idx, 1);
						});
					}
				} else { // not array
					if (params[hashgroup][property] == hash[property])
						delete params[hashgroup][property];
					else
						params[hashgroup][property] = hash[property];
				}
			}
		} else {
			$.extend(params[hashgroup], hash);
		}

		for (property in params[hashgroup]) {
		    if (params[hashgroup][property] == '')
		    	delete params[hashgroup][property];
		}

		AeJSEngine.detectHashChange = false;
		var val = $.param(params);
		if (val == "")
			// hash set to empty string cause page scroll up to top, set it to _ to prevent this behavior
			window.location.hash = "_";
		else
			window.location.hash = val;
	},

	// 合并参数，优先级从高到低为：指定的参数、hash参数、get参数
	mergeParam: function(wid) {
		var obj = {};

		var settings = AeJSEngine.getWinSettings(wid);
		if (settings.params != null)
			$.extend(obj, settings.params);

		var idx = window.location.href.indexOf('?');
		if (idx > 0)
			$.extend(obj, $.deparam(window.location.href.substr(idx + 1)));

		try {
			$.extend(obj, $.deparam(AeJSEngine.getHash(wid)));
		} catch (e) {
		}

		for (var i = 1; i < arguments.length; i++) {
			if (arguments[i] != null)
				$.extend(obj, arguments[i]);
		}

		return $.param(obj, true);
	},

	ensureVisible: function(id) {
		var $id = $(id);
		if ($id == null || $id.length == 0)
			return;

		try {
			var doc = document.documentElement; 
			var left = (window.pageXOffset || doc.scrollLeft) - (doc.clientLeft || 0);
			var top = (window.pageYOffset || doc.scrollTop)  - (doc.clientTop || 0);

			var rect = new ElmRect($id);
			rect.top -= AeJSEngine.topSpace;
			rect.bottom += AeJSEngine.bottomSpace;
			rect.left -= AeJSEngine.leftSpace;
			rect.right += AeJSEngine.rightSpace;

			var scrollX = 0;
			if (left + doc.clientWidth <  rect.right)
				scrollX = rect.right - left - doc.clientWidth;
			if (left + scrollX > rect.left)
				scrollX = rect.left - left;
		
			var scrollY = 0;
			if (top + doc.clientHeight <  rect.bottom)
				scrollY = rect.bottom - top - doc.clientHeight;
			if (top + scrollY > rect.top)
				scrollY = rect.top - top;
			if (scrollX != 0 || scrollY != 0)
				window.scrollBy(scrollX, scrollY);
		} catch (e) {
		}
	},

	clearLoading: function(id) {
		try {
			$(id + "_loading").remove();
		} catch (e) {
		}
	},

	showLoading: function(id, dialog) {
		try {
			AeJSEngine.clearLoading(id);

			var rect = new ElmRect($(id));
			try {
				if (dialog != null)
					rect = new ElmRect(dialog);
			} catch (e) {
			}

			var html;
			if (jQuery.browser.version == '6.0')
				html = "<div id='" + id.substr(1) + "_loading' style='z-index:100000;position:absolute;background-color:#999999;filter:alpha(opacity=30);-moz-opacity:0.3;left:" + rect.left + "px;top:" + rect.top
					+ "px;width:" + rect.width + "px;height:" + rect.height
					+ "px'><table width='100%' height='100%' border='0'><tr height='100%'><td align='center' valign='middle'><img src='"
					+ AeJSEngine.ImgLoading.src
					+ "'/></td></tr></table></div>";
			else
				html = "<div id='" + id.substr(1) + "_loading' style='z-index:100000;position:absolute;background:url(" + AeJSEngine.ImgBg.src
					+ ");left:" + rect.left + "px;top:" + rect.top
					+ "px;width:" + rect.width + "px;height:" + rect.height
					+ "px'><table width='100%' height='100%' border='0'><tr height='100%'><td align='center' valign='middle'><img src='"
					+ AeJSEngine.ImgLoading.src
					+ "'/></td></tr></table></div>";
			$("body").append(html);
		} catch (e) {
		}
	},

	procStyle: function(cont) {
		var css = cont.match(AeJSEngine.reCSSAll) || [];
		var cssHref = $.map(css, function(tag) {return (tag.match(AeJSEngine.reCSSHref) || ['', ''])[1];});

		var elmHead = document.getElementsByTagName("head")[0];
		var elmLinks = elmHead.getElementsByTagName("link");
		var i;
		var j;
		var newCss;

		for (i = 0; i < cssHref.length; i++) {
			if (cssHref[i] == "")
				continue;
		
			for (j = 0; j < elmLinks.length; j++) {
				if (elmLinks[j].href == cssHref[i])
					break;
			}
		
			if (j < elmLinks.length)
				continue;

			newCss = document.createElement('link');
			newCss.type = 'text/css';
			newCss.rel = 'stylesheet';
			newCss.href = cssHref[i];
			newCss.media = 'screen';
			elmHead.appendChild(newCss);
			}
		
		return cont.replace(AeJSEngine.reCSSAll, '');
	},

	procWinFunc: function(cont, wid) {
		return cont.replace(AeJSEngine.reWinPost, 'win$._post(' + wid + ', ')
			.replace(AeJSEngine.reWinWid, 'win$._wid(' + wid + ')')
			.replace(AeJSEngine.reWinAjax, 'win$._ajax(' + wid + ', ')
			.replace(AeJSEngine.reWinGet, 'win$._get(' + wid + ', ')
			.replace(AeJSEngine.reWinToggle, 'win$._toggle(' + wid + ', ')
			.replace(AeJSEngine.reWinUrl, 'win$._url(' + wid + ', ')
			.replace(AeJSEngine.reWinSubmit, 'win$._submit(' + wid + ', ')
			.replace(AeJSEngine.reWinAfterSubmit, 'win$._aftersubmit(' + wid + ', ');
	},

     procScript: function(wid, cont) {
		var scripts = cont.match(AeJSEngine.reScriptAll) || [];
		var scriptContent = $.map(scripts, function(scriptTag) {return (scriptTag.match(AeJSEngine.reScriptOne) || ['', '', ''])[2];});
		var scriptDef = $.map(scripts, function(scriptTag) {return (scriptTag.match(AeJSEngine.reScriptOne) || ['', '', ''])[1];});
		var scriptLanguage = $.map(scriptDef, function(scriptTag) {return (scriptTag.match(AeJSEngine.reScriptLanguage) || ['', ''])[1];});
		var scriptSrc = $.map(scriptDef, function(scriptTag) {return (scriptTag.match(AeJSEngine.reScriptSrc) || ['', ''])[1];});
		var scriptType = $.map(scriptDef, function(scriptTag) {return (scriptTag.match(AeJSEngine.reScriptType) || ['', ''])[1];});
		var scriptCharset = $.map(scriptDef, function(scriptTag) {return (scriptTag.match(AeJSEngine.reScriptCharset) || ['', ''])[1];});

		var elmHead = document.getElementsByTagName("head")[0];
		var elmScripts = elmHead.getElementsByTagName("script");
		var i;
		var j;
        var ret = [];

		for (i = 0; i < scripts.length; i++) {
			if (scriptSrc[i] == "")
				continue;

			for (j = 0; j < elmScripts.length; j++) {
				if (elmScripts[j].src == scriptSrc[i])
					break;
			}
		
			if (j < elmScripts.length)
				continue;

            var dtd = $.Deferred();

            var newScript = document.createElement('script');
			if (scriptType[i] != "")
				newScript.type = scriptType[i];
			else if (scriptLanguage[i] != "")
				newScript.type = "text/" + scriptLanguage[i];
			else
				newScript.type = "text/javascript";
			if (scriptCharset[i] != "")
				newScript.charset = scriptCharset[i];

            $(newScript).load(function() {
                dtd.resolve();
            }).on('readystatechange', function(){
                if (newScript.readyState == 'loaded'){
                    dtd.resolve();
                }
            });

			elmHead.appendChild(newScript);
			newScript.src = scriptSrc[i];
            ret[ret.length] = dtd.promise();
		}

		$.when.apply($, ret).done(function() {
			for (i = 0; i < scriptContent.length; i++)
				try {
					eval(AeJSEngine.procWinFunc(scriptContent[i], wid));
				} catch (e) {
					alert(e.message);
					alert(scriptContent[i]);
				}
		});
	},

	invokeAfterLoad: function() {
		if (AeJSEngine.afterLoad) {
			try {
				AeJSEngine.afterLoad();
			} catch (e) {
			}
		}
	},

	enableForm: function(container) {
		container.find("form[data-winlet-wid]").each(function() {
			var form = $(this);
			form.winform({
				wid: form.attr("data-winlet-wid"),
				focus: form.attr("data-winlet-focus"),
				update: form.attr("data-winlet-update"),
				validate: form.attr("data-winlet-validate"),
				hideloading: form.attr("data-winlet-hideloading")
				});
		});
	},

	/**
	 * Window方法执行返回结果的处理
	 * 
	 * @param wid
	 * @param focus
	 * @returns {Function}
	 */
	getWindowResponseHandler: function(wid, focus) {
		return function (data, textStatus, jqXHR) {
			var redirect = jqXHR.getResponseHeader('X-Winlet-Redirect'); 
	        if (redirect != null && redirect != "") {
	            window.location.href = redirect;
	            return;
	        }

			var uid = '#ap_win_' + wid;
	        var container = $(uid);

	        var dialog = false;
	        if (AeJSEngine.isRootWinId(wid) && AeJSEngine.getWinSettings(wid).dialog == "yes") {
	        	dialog = true;

	        	var title = AeJSEngine._utf8_decode(jqXHR.getResponseHeader('X-Winlet-Title'));
	        	AeJSEngine.openDialog(false, wid, data, title);
	        } else {
				container.html(
						AeJSEngine.procStyle(
								AeJSEngine.procWinFunc(data.replace(AeJSEngine.reScriptAll, ''), wid))
						);
				$(function() {
					AeJSEngine.enableForm(container);
				});

				AeJSEngine.procScript(wid, data);
	        }

			AeJSEngine.invokeAfterLoad();
			AeJSEngine.clearLoading(uid);
			container.trigger("AeWindowLoaded", wid);

			if (!dialog && focus)
				AeJSEngine.ensureVisible(uid);
		};
	},

	loadContent: function(wid, focus, pageRefresh) {
		var uid = '#ap_win_' + wid;

		if ($(uid).length == 0)
			return;

		AeJSEngine.showLoading(uid);

		$.ajax({
			  type: 'POST',
			  url: AeJSEngine.getWinSettings(wid).url,
			  data: AeJSEngine.mergeParam(wid, {_x: 'y', _w: wid, _pg: window.location.pathname, _purl: window.location.href, _pr: pageRefresh ? "yes" : "no"}),
			  success: AeJSEngine.getWindowResponseHandler(wid, focus),
			  error: AeJSEngine.getErrorHandler(),
			  dataType: "html"
			});
	},

	/**
	 * Action方法执行返回结果的处理
	 * 
	 * @param wid
	 * @returns {Function}
	 */
	getActionResponseHandler: function(wid) {
		var form = null;
		var funcs = null;

		for (var i = 1; i < arguments.length; i++) {
			if (arguments[i] == null)
				continue;

			// 参数如果是对象数组，则视为是回调函数数组
			if (Object.prototype.toString.call(arguments[i]) == '[object Array]')
				funcs = arguments[i];
			else // 如果不是对象数组，则视为被提交的表单对象
				form = arguments[i];
		}

		return function (data, textStatus, jqXHR) {
			var redirect = jqXHR.getResponseHeader('X-Winlet-Redirect'); 
	        var update = jqXHR.getResponseHeader('X-Winlet-Update');
			var dialog = jqXHR.getResponseHeader('X-Winlet-Dialog');
			var cache = jqXHR.getResponseHeader('X-Winlet-Cache');
			var title = AeJSEngine._utf8_decode(jqXHR.getResponseHeader('X-Winlet-Title'));
			// var msg = jqXHR.getResponseHeader('X-Winlet-Msg');

	        if (redirect != null && redirect != "")
	            window.location.href = redirect;

			if (update == "page") {
				location.reload();
				return;
			}

			var uid = '#ap_win_' + wid;

			AeJSEngine.clearLoading(uid);

			// 只有处理表单提交响应时form参数才不为null。如果时直接调用action或者翻译窗口url，form参数都为空
			if (form != null && dialog != "yes" && data.indexOf("WINLET_FORM_RESP:") == 0) {
				// 提交表单并且表单校验出错
				AeJSEngine.form.validateClearAll(form);
				AeJSEngine.form.applyChanges(data.substr(17), form);

				if (form.onerror != undefined) {
					try {
						form.onerror(null);
					} catch (e) {
					}
				}
				return;
			}

			if (funcs != null) {
				for (var i = 0; i < funcs.length; i++) {
					try {
						var ret = funcs[i](data, textStatus, jqXHR);

						// 函数返回false则停止处理
						if (ret != undefined && ret != null && !ret)
							return;
					} catch (e) {
						alert(e);
					}
				}
			}

			if (!(cache == "yes"))
				AeJSEngine.loadContent(wid);

			// Update window
			while (update != null && update != "") {
				var iposi = update.indexOf(",");
				var window = update;
				if (iposi != -1) {
					window = update.substr(0, iposi);
					update = update.substr(iposi + 1);
				} else {
					window = update;
					update = "";
				}

				var focus = false;
				if (window.indexOf("!") == 0) {
					focus = true;
					window = window.substring(1);
				}

				if (wid != window)
					AeJSEngine.loadContent(window, focus);
			}

			if (dialog == "yes")
				AeJSEngine.openDialog(true, wid, data, title);
			else
				AeJSEngine.closeDialog();
		};
	},

	updateWindows: function(wid, wins) {
		if (wins == null || wins == '')
			return;

		var update = wins.split(',');
		var i;
		var unknown = null;
		for (i = 0; i < update.length; i++) {
			try {
				var ud = update[i];
				var focus = false;
				if (ud.indexOf("!") == 0) {
					focus = true;
					ud = ud.substring(1);
				}
				var updatewid = AeJSEngine.hashGroupByRootWindow[AeJSEngine.getRootWinId(wid)].views[ud];
				if (updatewid != null)
					AeJSEngine.loadContent(updatewid, focus);
				else {
					if (unknown == null)
						unknown = update[i];
					else {
						unknown = unknown + ", " + update[i];
					}
				}
			} catch(e) {
			}
		}

		if (unknown != null) { // 不是顶级窗口，请求服务端将名称翻译为wid
			$.ajax({
				type: 'POST',
				url: AeJSEngine.getWinSettings(wid).url,
				data: AeJSEngine.mergeParam(wid, {_x: 'y', _w: wid, _u: unknown, _pg: window.location.pathname, _purl: window.location.href}),
				success: AeJSEngine.getActionResponseHandler(wid),
				error: AeJSEngine.getErrorHandler(wid),
				dataType: "html"
			});
		}
	},

	// 简单的错误处理 － 刷新当前页面
	getErrorHandler: function(wid) {
		return function(req, textStatus, errorThrown) {
			// document.location.reload(true);
		};
	},

	isInt: function(n){
        return n != undefined && n != null && Number(n) === n && n % 1 === 0;
	},

	/******************************************************************************
	 *
	 *  扫描<div id="winlet:">标签，在其中生成<div id="ap_win_">标签，并为生成的标签设置settings属性对象。
	 *  settings中可以包含以下属性：
	 *  	hashgroup	该window所属的参数组。相同组的window共享hash参数
	 *  	dialog		如果值为yes表示用弹出对话框显示窗口
	 *  	close		对于弹出对话框显示的窗口，关闭窗口时调用的Winlet的方法
	 *  	url			Winlet的窗口的URL
	 *  
	 *  根窗口才有settings和hashgroup，子窗口共享根窗口的settings和hashgroup
	 *
	 ******************************************************************************/
	init: function(settings) {
		// { 借助样式获取图片文件URL
		$("body").append("<div id='winlet_style_temp' style='display:none'><div class='winlet_background'>1</div><div class='winlet_loading'>2</div><div class='winlet_validating'>3</div></div>");
		$(function() {
			AeJSEngine.ImgBg.src = $("#winlet_style_temp .winlet_background").css('background-image').replace(/^url|[\(\)"]/g, ''); 
			AeJSEngine.ImgLoading.src = $("#winlet_style_temp .winlet_loading").css('background-image').replace(/^url|[\(\)"]/g, ''); 
			AeJSEngine.ImgValidating.src = $("#winlet_style_temp .winlet_validating").css('background-image').replace(/^url|[\(\)"]/g, '');
			console.log(AeJSEngine.ImgLoading.src);
			$("#winlet_style_temp").remove();
		});
		// }

		if (settings) {
			if (AeJSEngine.isInt(settings.left))
				AeJSEngine.leftSpace = settings.left;
			if (AeJSEngine.isInt(settings.right))
				AeJSEngine.rightSpace = settings.right;
			if (AeJSEngine.isInt(settings.top))
				AeJSEngine.topSpace = settings.top;
			if (AeJSEngine.isInt(settings.bottom))
				AeJSEngine.bottomSpace = settings.bottom;
		}

		AeJSEngine.isStatic = true;

		var process = {
			preload: true,	// true表示处理预加载的winlet，false表示处理非预加载
			start: 0, 		// idx起始
			maxIdx: 0		// 最大的idx值
		}; 

		var func = function(idx) {
			process.maxIdx = idx;
			idx = idx + process.start;

			var wid = $(this).data("wid");
			if (wid == null) {
				if (process.preload)
					return;
				wid = idx.toString().length.toString() + idx;
			} else {
				if (!process.preload)
					return;
			}

			var match = $(this).data("winlet").match(AeJSEngine.reWinlet);
			var hashgroup = AeJSEngine.hashGroupsByUri[match[2]];
			if (hashgroup == null) {
				hashgroup = {
						"idx": idx,
						views: {}};
				AeJSEngine.hashGroupsByUri[match[2]] = hashgroup;
			}

			hashgroup.views[match[3]] = wid;
			AeJSEngine.hashGroupByRootWindow[wid] = hashgroup;

			var winDiv = $(this).children("div#ap_win_" + wid);
			if (winDiv.length == 0) {
				winDiv = $("<div id='ap_win_" + wid + "'></div>");
				$(this).append(winDiv);
			}

			winDiv[0].settings = {"hashgroup": hashgroup.idx, "dialog": "no", "close": "", "url": "", "params": match[5] == null ? null : $.deparam(match[5])};

			if (match.length > 7 && match[7] != null && match[7] != '') {
				var params = match[7].split(',');
				var i;

				for (i = 0; i < params.length; i++) {
					var pmatch = params[i].match(AeJSEngine.reWinletParam);
					winDiv[0].settings[pmatch[1]] = pmatch[2];
				}
			}

			winDiv[0].settings.url = "/" + match[1];

			if (process.preload) {
				// 预加载winlet内容中可能会包含对其他winlet的客户端引用，因此需要先把
				// 预加载的winlet处理完毕后再处理非预加载的winlet
				winDiv.html(AeJSEngine.procWinFunc(winDiv.html()));
				$(function() {
					AeJSEngine.enableForm(winDiv);
				});
			} else {
				AeJSEngine.loadContent(wid, false, true);
			}
		};

		// 第一轮先处理预加载的Winlet
		$('div[data-winlet]').each(func);
		
		// 第二轮处理其他Winlet
		process.preload = false;
		process.start = process.maxIdx + 1;
		$('div[data-winlet]').each(func);
	}
};

var win$ = {
	/**
	 * 将参数转成JSON格式。
	 * 
	 * @param params 要转换的对象，可以为表单名称，表单对象，JSON数据或URL param格式的字符串
	 * @returns
	 */
	getParams: function(params, wid) {
		try {
			if (typeof params == "string") { // 类型为字符串
				if (params.indexOf("{") == 0) { // json字符串
					return $.parseJSON(params);
				} if (params.indexOf("=") > 0) // URL param格式字符串
					return $.deparam(params);
				else // params为form name
					params = $('form[name="' + params + wid + '"]');
			}

			if (params.is('form'))
				return $.deparam(params.serialize());
		} catch (e) {
		}

		return params;
	},
	
	_wid: function(wid) {
		return wid;
	},

	_post: function(wid, action) {
		var params = {};
		var funcs = [];

		if (typeof action == "object") {
			if (action.hash != null)
				AeJSEngine.setHash(wid, action.hash);
			action = action.action;
		}

		for (var i = 2; i < arguments.length; i++) {
			if (arguments[i] == null)
				continue;

			if (typeof arguments[i] === 'function')
				funcs[funcs.length] = arguments[i];
			else
				$.extend(params, win$.getParams(arguments[i], wid));
		}

		$.ajax({
			type: 'POST',
			url: AeJSEngine.getWinSettings(wid).url,
			data: AeJSEngine.mergeParam(wid, params, {_x: 'y', _w: wid, _a: action, _pg: window.location.pathname, _purl: window.location.href}),
			success: AeJSEngine.getActionResponseHandler(wid, funcs),
			error: AeJSEngine.getErrorHandler(wid),
			dataType: "html"
		});
	},
	
	_ajax: function(wid, paramFunc) {
		$.ajax(paramFunc(wid));
	},

	_get: function(wid, param) {
		var reload = true;
		var update = null;

		if (param != null)
			if (typeof param == "object") {
				for (var prop in param) {
					if ("reload" == prop)
						reload = param[prop];
					if ("update" == prop)
						update = param[prop];
				}
			} else {
				update = param;
			}

		var params = {};

		for (var i = 2; i < arguments.length; i++) {
			$.extend(params, win$.getParams(arguments[i], wid));
		}

		var focusUpdate = false;
		if (update && update.indexOf("!") >= 0)
			focusUpdate = true;

		AeJSEngine.setHash(wid, params);
		if (reload)
			AeJSEngine.loadContent(wid, !focusUpdate);
		if (update)
			AeJSEngine.updateWindows(wid, update);
	},
	
	_toggle: function(wid, update) {
		var params = {};
		
		for (var i = 2; i < arguments.length; i++) {
			$.extend(params, win$.getParams(arguments[i], wid));
		}

		AeJSEngine.setHash(wid, params, true);

		AeJSEngine.loadContent(wid);
		AeJSEngine.updateWindows(wid, update);
	},

	_url: function(wid, action) {
		var params = {};

		for (var i = 2; i < arguments.length; i++)
			$.extend(params, win$.getParams(arguments[i], wid));

		return AeJSEngine.getWinSettings(wid).url + "?" + AeJSEngine.mergeParam(wid, params, {_x: 'y', _w: wid, _a: action, _pg: window.location.pathname, _purl: window.location.href});
	},
	
	reAction: new RegExp('^(.*)!(.*)$'),

	_submit: function(wid, form, action) {
		var f = $('form[name="' + form + wid + '"]');
		if (f.length != 1)
			return;
		
		if (action != null && action != '') {
			if (!f.attr('action').match(win$.reAction))
				return;
			f.attr('action', f.attr('action').replace(win$.reAction, "$1!" + action));
		}

		var params = {};
		for (var i = 3; i < arguments.length; i++) {
			if (arguments[i] == null)
				continue;

			$.extend(params, win$.getParams(arguments[i], wid));
		}

		for (var key in params) {
			f.find("input[name=" + key + "]").attr("value", params[key]);
		}

		f.submit();
	},

	_aftersubmit: function(wid, form) {
		var f = $('form[name="' + form + wid + '"]');
		if (f.length != 1)
			return;
		
		var funcs = [];
		for (var i = 2; i < arguments.length; i++) {
			if (arguments[i] == null)
				continue;

			if (typeof arguments[i] === 'function')
				funcs[funcs.length] = arguments[i];
		}
		
		f[0].aftersubmit = funcs;
	},

	/**
	 * 返回一个promise，用于等待页面中出现selector对应的元素，等待时间不超过maxwait
	 */
	wait: function(selector, maxwait) {
		var start = (new Date()).getTime();
        var dtd = $.Deferred();

		(function() {
			if ($(selector) == undefined) {
				if (maxwait != null && (new Date()).getTime() - start > maxwait)
					dtd.reject();
				else
					window.setTimeout(arguments.callee, 300);
			}
			else {
				dtd.resolve();
			}
		})();

		return dtd.promise();
	}
};

$(window).hashchange(function(){
	if (AeJSEngine.detectHashChange)
		AeJSEngine.init();
	else
		AeJSEngine.detectHashChange = true;
});
