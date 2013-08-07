package com.aggrepoint.winlet.form;

import java.lang.reflect.Method;
import java.util.Vector;

import net.sf.json.JSONNull;
import net.sf.json.JSONSerializer;

/**
 * 输入表单
 * 
 * @author Jim
 */
public class FormImpl implements Form {
	/** 表单的ID */
	String id;
	/** 表单对应的操作 */
	String strAction;
	/** 表单中的输入项 */
	Vector<InputImpl> vecInputs = new Vector<InputImpl>();
	/** 进行单项校验的输入项 */
	InputImpl validateField;
	/** 是否存在校验错误 */
	protected boolean bHasError;
	/** 对应的Winlet */
	Object winlet;
	/** 重置依赖对象。如果对象实例变化了，则需要重置表单内容 */
	Object objResetRef;
	/** 在一次校验请求处理过程中表单的变化 */
	Vector<Change> vecChanges;
	Method actionMethod;
	/**
	 * 尝试将当前表单失效的请求的id。
	 * 用于在表单没有制定resetref的情况下，实现自动清空表单
	 * 
	 * window方法执行时，会将本视图所属表单中，invalidateRequestId为0的，全都设置为当前请求id。
	 * 
	 * JSP页面显示表单时，如果发现invalidateRequestId与当前请求id一致，表示表单在本次请求被使用了，不需要进行表单重置，
	 * 并且将invalidateRequestId改回为0. JSP页面显示表单时，如果发现invalidateRequestId与当前请求id不一致，
	 * 说明对应的window方法曾经执行过并且表单没有被使用，将表单重置。
	 * 
	 */
	long invalidateRequestId;

	public FormImpl(String id, Object winlet) {
		this.id = id;
		this.winlet = winlet;
	}

	public void setInvalidateRequestId(long l) {
		if (l == 0 || invalidateRequestId == 0)
			invalidateRequestId = l;
	}

	public long getInvalidateRequestId() {
		return invalidateRequestId;
	}

	public void startRecordChanges() {
		vecChanges = new Vector<Change>();
	}

	public void recordChange(Change change) {
		if (vecChanges != null)
			change.addTo(vecChanges);
	}

	public void removeChange(Change change) {
		if (vecChanges != null)
			vecChanges.removeAll(Change.find(vecChanges, change.type,
					change.input));
	}

	public String getId() {
		return id;
	}

	public void setAction(String a) {
		strAction = a;
	}

	public String getAction() {
		return strAction;
	}

	public void setResetRef(Object a) {
		objResetRef = a;
	}

	public Object getResetRef() {
		return objResetRef;
	}

	@Override
	public boolean hasError() {
		return bHasError;
	}

	public void clearError() {
		bHasError = false;
		for (InputImpl inp : vecInputs)
			inp.clearErrors();
	}

	@Override
	public Form reset() {
		vecInputs = new Vector<InputImpl>();
		validateField = null;
		bHasError = false;
		actionMethod = null;
		return this;
	}

	public Vector<InputImpl> getInputs() {
		return vecInputs;
	}

	public void updateErrorFlag() {
		bHasError = false;

		for (InputImpl input : vecInputs)
			if (input.isHasError()) {
				bHasError = true;
				return;
			}
	}

	public InputImpl getInput(String type, String name) {
		for (InputImpl inp : vecInputs)
			if (inp.getName().equalsIgnoreCase(name)) {
				if (inp.winlet == winlet && inp.getType().equals(type))
					return inp;
				vecInputs.remove(inp);
				break;
			}
		InputImpl inp = InputImpl.getInstance(this, type, winlet, name);
		vecInputs.add(inp);
		return inp;
	}

	@Override
	public InputImpl getInputByName(String name) {
		for (InputImpl inp : vecInputs)
			if (inp.getName().equalsIgnoreCase(name))
				return inp;
		return null;
	}

	@Override
	public boolean isValidateField() {
		return validateField != null;
	}

	@Override
	public InputImpl getValidateField() {
		return validateField;
	}

	public void setValidateField(InputImpl input) {
		validateField = input;
	}

	@Override
	public Input disable(String name) {
		InputImpl input = getInputByName(name);
		if (input != null) {
			input.setDisabled(true);
			recordChange(new ChangeDisable(name));
		}
		return input;
	}

	@Override
	public Input enable(String name) {
		InputImpl input = getInputByName(name);
		if (input != null) {
			input.setDisabled(false);
			recordChange(new ChangeEnable(name));
		}
		return input;
	}

	@Override
	public Input setDisabled(String name, boolean disabled) {
		if (disabled)
			return disable(name);
		return enable(name);
	}

	public String getJsonChanges() {
		if (vecChanges == null)
			return JSONSerializer.toJSON(JSONNull.getInstance()).toString();

		return JSONSerializer.toJSON(
				vecChanges.toArray(new Change[vecChanges.size()])).toString();
	}

	public void bindAction(Method actionMethod) {
		if (this.actionMethod == actionMethod)
			return;
		this.actionMethod = actionMethod;
		for (InputImpl input : vecInputs)
			input.bindAction(actionMethod);
	}
}
