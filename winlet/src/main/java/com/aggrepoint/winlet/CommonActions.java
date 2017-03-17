package com.aggrepoint.winlet;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.ui.Model;

import com.aggrepoint.winlet.form.Form;

/**
 * Controller通用的处理逻辑
 * 
 * @author jiangmingyang
 *
 */
public class CommonActions {
	/**
	 * 启动添加或编辑
	 * 
	 * @param model
	 * @param id
	 *            要编辑的数据的id
	 * @param find
	 *            根据id加载数据
	 * @param create
	 *            新建数据实例
	 * @param attrName
	 *            数据放入model使用的属性名称
	 * @param editView
	 *            数据编辑视图
	 * @return
	 */
	public static <T> String edit(Model model, Integer id,
			Function<Integer, T> find, Supplier<T> create, String attrName,
			String editView) {
		if (id == null)
			return "";

		T obj = null;
		if (id == 0)
			obj = create.get();
		else
			obj = find.apply(id);

		if (obj == null)
			return "notfound";

		model.addAttribute(attrName, obj);
		return editView;
	}

	/**
	 * 删除记录，删除前先进行确认
	 * 
	 * @param model
	 * @param id
	 *            要删除的数据的id
	 * @param confirm
	 *            大于0表示用户已经确认
	 * @param find
	 *            根据id查找要删除的数据对象
	 * @param delete
	 *            根据id删除数据对象
	 * @param message
	 *            用数据对象构造提示信息
	 * @param action
	 *            /common/confirm.jsp的ACTION参数
	 * @param params
	 *            /common/confirm.jsp的PARAMS参数
	 * @return
	 */
	public static <T> String deleteAfterConfirm(Model model, Integer id,
			Integer confirm, Function<Integer, T> find,
			Consumer<Integer> delete, Function<T, String> message,
			String action, String idparam) {
		if (id == null || id == 0) // 没有接收到ID
			return "";

		T t = find.apply(id);
		if (t == null) // 找不到要删除的对象
			return "";

		if (confirm != null && confirm > 0) { // 用户已经确认删除
			delete.accept(id);
			return "deleted";
		} else { // 请用户确认删除
			model.addAttribute("MESSAGE", message.apply(t));
			model.addAttribute("ACTION", action);
			model.addAttribute("PARAMS", idparam + ": " + id + ", confirm: 1");
			return "/common/confirm";
		}
	}

	public static <T> String deleteAfterConfirm(Model model, Integer confirm,
			Supplier<T> find, Runnable delete,
			Function<T, String> message, String action, String idparam, Long id) {
		if (id == null) // 没有接收到ID
			return "";

		T t = find.get();
		if (t == null) // 找不到要删除的对象
			return "";

		if (confirm != null && confirm > 0) { // 用户已经确认删除
			delete.run();
			return "deleted";
		} else { // 请用户确认删除
			model.addAttribute("MESSAGE", message.apply(t));
			model.addAttribute("ACTION", action);
			model.addAttribute("PARAMS", idparam + ": " + id + ", confirm: 1");
			return "/common/confirm";
		}
	}

	/**
	 * 保存数据
	 * 
	 * @param form
	 * @param object
	 * @param save
	 * @return
	 */
	public static <T> String save(Form form, T object, Consumer<T> save) {
		if (form.isValidateField())
			return form.hasError() ? "vf_error" : "vf";

		if (form.hasError())
			return "error";

		save.accept(object);

		return "";
	}

	public static <T> String save(Form form, Supplier<String> save) {
		if (form.isValidateField())
			return form.hasError() ? "vf_error" : "vf";

		if (form.hasError())
			return "error";

		return save.get();
	}
}
