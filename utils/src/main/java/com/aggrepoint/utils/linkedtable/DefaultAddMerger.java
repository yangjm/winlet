package com.aggrepoint.utils.linkedtable;

import com.aggrepoint.utils.TypeCast;

public class DefaultAddMerger implements INodeMerger {
	private static final DefaultAddMerger INSTANCE = new DefaultAddMerger();

	private DefaultAddMerger() {

	}

	public static DefaultAddMerger getInstance() {
		return INSTANCE;
	}

	public <T> T merge(T a, T b) {
		if (a == null)
			return b;
		if (b == null)
			return a;

		if (a instanceof Double) {
			double va = 0.0d;
			if (a != null)
				va = TypeCast.cast(a);

			double vb = 0.0d;
			if (b != null)
				vb = TypeCast.cast(b);
			return TypeCast.cast(new Double(va + vb));
		} else if (a instanceof Integer) {
			int va = 0;
			if (a != null)
				va = TypeCast.cast(a);

			int vb = 0;
			if (b != null)
				vb = TypeCast.cast(b);
			return TypeCast.cast(new Double(va + vb));
		}

		return null;
	}

}
