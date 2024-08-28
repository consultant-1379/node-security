package com.ericsson.nms.security.nscs.api.util;

public class NscsPair<L, R> {

	private L l;
	private R r;

	public NscsPair(L l, R r) {
		super();
		this.l = l;
		this.r = r;
	}

	public L getL() {
		return l;
	}

	public void setL(final L l) {
		this.l = l;
	}

	public R getR() {
		return r;
	}

	public void setR(final R r) {
		this.r = r;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.l == null) ? 0 : this.l.hashCode());
		result = prime * result + ((this.r == null) ? 0 : this.r.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NscsPair)) {
			return false;
		}
		final NscsPair<?, ?> other = (NscsPair<?, ?>) obj;
		if (this.l == null) {
			if (other.getL() != null) {
				return false;
			}
		} else if (!this.l.equals(other.getL())) {
			return false;
		}
		if (this.r == null) {
			if (other.getR() != null) {
				return false;
			}
		} else if (!this.r.equals(other.getR())) {
			return false;
		}
		return true;
	}

}
