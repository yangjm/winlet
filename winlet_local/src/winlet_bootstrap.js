AeJSEngine.getDialog = function(wid) {
	if (wid != null) {
		var dlg = $("div#ap_win_" + wid + "_dialog");
		if (dlg.length == 0) {
			dlg = $('<div class="modal fade" id="ap_win_' + wid + '_dialog" tabindex="-1" role="dialog" aria-hidden="true"><div class="modal-dialog"><div class="modal-content"><div class="modal-header"><button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button><h4 class="modal-title">&nbsp;</h4></div><div class="modal-body">Body</div><div class="modal-footer">&nbsp;</div></div></div></div>');
			$("div#ap_win_" + wid).append(dlg);
		}
		
		return dlg;
	} else {
		if (AeJSEngine.dlg == null) {
			AeJSEngine.dlg = $('<div class="modal fade" id="AeDialog" tabindex="-1" role="dialog" aria-hidden="true"><div class="modal-dialog"><div class="modal-content"><div class="modal-header"><button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button><h4 class="modal-title">&nbsp;</h4></div><div class="modal-body">Body</div><div class="modal-footer">&nbsp;</div></div></div></div>');
			$(document.body).append(AeJSEngine.dlg);
		}
		
		return AeJSEngine.dlg;
	}
}

AeJSEngine.closeDialog = function(wid) {
	var dlg = AeJSEngine.getDialog(wid);

	try {
		if (dlg.hasClass('in')) // http://stackoverflow.com/questions/19674701/can-i-check-if-bootstrap-modal-shown-hidden
			dlg.modal('hide');
	} catch (e) {
	}
	dlg.find("div.modal-body").empty();
};

AeJSEngine.openDialog = function(shared, wid, content, title) {
	var dlg = AeJSEngine.getDialog(shared ? null : wid);

	var body = dlg.find("div.modal-body"); 
	var html = $.trim(AeJSEngine.procStyle(AeJSEngine.procWinFunc(content.replace(AeJSEngine.reScriptAll, '')
			.replace(AeJSEngine.reDialogSetting, ''), wid)));

	if (!shared && html == '') {
		AeJSEngine.closeDialog(wid);
		return;
	}

	body.empty().append(html);

	var settings = AeJSEngine.reDialogSetting.exec(content);
	if (settings != null) {
		settings = JSON.parse(AeJSEngine.procWinFunc(settings[1], wid));
		dlg.find("h4.modal-title").empty().append(settings.title);

		var footer = dlg.find("div.modal-footer").empty();
		for (var i = 0; i < settings.buttons.length; i++) {
			var button = "<button";
			for (var prop in settings.buttons[i]) {
				if (prop != 'label')
					button = button + " " + prop + "=\"" + settings.buttons[i][prop] + "\"";
			}

			button = button + ">" + settings.buttons[i].label + "</button>";
			footer.append(button);
		}
	} else if (title != null) {
		dlg.find("h4.modal-title").empty().append(title);
	}

	$(function() {
		var focus = null;

		body.find("form[data-winlet-wid]").each(function() {
			var form = $(this);
			form.winform({
				wid: form.attr("data-winlet-wid"),
				focus: form.attr("data-winlet-focus"),
				update: form.attr("data-winlet-update"),
				validate: form.attr("data-winlet-validate"),
				hideloading: form.attr("data-winlet-hideloading"),
				dialog: dlg});

			if (form.attr("data-winlet-focus"))
				focus = form.find('input[name="' + form.attr("data-winlet-focus") + '"]');
		});

		AeJSEngine.procScript(wid, content);

		if (focus) {
			dlg.off('shown.bs.modal').on('shown.bs.modal', function () {
			    focus.select();
			    focus.focus();
			});
		}

		dlg.modal('show');
	});
};

AeJSEngine.form.validateSuccess = function(input) {
	var result = AeJSEngine.form.getInputResult(input);
	if (result != null) {
		var parents = $(input).parents("div.form-group");
		if (parents.length > 0)
			$(parents[0]).removeClass("has-error").addClass("has-success");
	}
};

AeJSEngine.form.validateError = function() {
	var original = AeJSEngine.form.validateError;
	return function(input, msg) {
		original(input, msg);

		var result = AeJSEngine.form.getInputResult(input);
		if (result != null) {
			var parents = $(input).parents("div.form-group");
			if (parents.length > 0)
				$(parents[0]).removeClass("has-success").addClass("has-error");
		}
	};
}();
