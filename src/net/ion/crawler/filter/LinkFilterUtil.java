package net.ion.crawler.filter;

import net.ion.crawler.link.Link;

public final class LinkFilterUtil {

	private LinkFilterUtil() {
	}

	// --- AND LinkFilter ---

	public static ILinkFilter and(ILinkFilter filter1, ILinkFilter filter2) {
		return new And(new ILinkFilter[] { filter1, filter2 });
	}

	public static ILinkFilter and(ILinkFilter[] filters) {
		return new And(filters);
	}

	private static class And implements ILinkFilter {

		private ILinkFilter[] filters;

		public And(ILinkFilter[] filters) {
			this.filters = filters;
		}

		public boolean accept(Link link) {
			for (ILinkFilter filter : filters) {
				if (filter == null) continue ;
				if (filter.accept(link) == false) {
					return false;
				}
			}
			return true;
		}

	}

	// --- OR LinkFilter ---

	public static ILinkFilter or(ILinkFilter filter1, ILinkFilter filter2) {
		return new Or(new ILinkFilter[] { filter1, filter2 });
	}

	public static ILinkFilter or(ILinkFilter[] filters) {
		return new Or(filters);
	}

	private static class Or implements ILinkFilter {

		private ILinkFilter[] filters;

		public Or(ILinkFilter[] filters) {
			this.filters = filters;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(Link link) {
			for (int i = 0; i < filters.length; i++) {
				if (filters[i].accept(link)) {
					return true;
				}
			}
			return false;
		}

	}

	// --- NOT LinkFilter ---

	public static ILinkFilter not(ILinkFilter filter) {
		return new Not(filter);
	}

	private static class Not implements ILinkFilter {

		private ILinkFilter filter;

		public Not(ILinkFilter filter) {
			this.filter = filter;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean accept(Link link) {
			return !filter.accept(link);
		}

	}

	// --- XOR LinkFilter ---

	public static ILinkFilter xor(ILinkFilter filter1, ILinkFilter filter2) {
		return new Xor(filter1, filter2);
	}

	private static class Xor implements ILinkFilter {

		private ILinkFilter f1;
		private ILinkFilter f2;

		public Xor(ILinkFilter f1, ILinkFilter f2) {
			this.f1 = f1;
			this.f2 = f2;
		}

		public boolean accept(Link link) {
			final boolean acceptF1 = f1.accept(link);
			final boolean acceptF2 = f2.accept(link);

			return ((acceptF1 && !acceptF2) || (!acceptF1 && acceptF2));
		}

	}

}
