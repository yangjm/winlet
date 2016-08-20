package com.aggrepoint.utils.linkedtable;

import com.aggrepoint.utils.TypeCast;

public class DefaultMinusMerger implements INodeMerger {
	private static final DefaultMinusMerger INSTANCE = new DefaultMinusMerger();

	private DefaultMinusMerger() {

	}

	public static DefaultMinusMerger getInstance() {
		return INSTANCE;
	}

	public <T> T merge(T from, T to) {
		if (from == null)
			return to;
		if (to == null)
			return from;

		if (from instanceof Double) {
			double vfrom = 0.0d;
			if (from != null)
				vfrom = TypeCast.cast(from);

			double vto = 0.0d;
			if (to != null)
				vto = TypeCast.cast(to);
			return TypeCast.cast(new Double(vto - vfrom));
		} else if (from instanceof Integer) {
			int vfrom = 0;
			if (from != null)
				vfrom = TypeCast.cast(from);

			int vto = 0;
			if (to != null)
				vto = TypeCast.cast(to);
			return TypeCast.cast(new Double(vto - vfrom));
		}

		return null;
	}

}
