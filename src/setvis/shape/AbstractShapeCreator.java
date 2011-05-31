/**
 * 
 */
package setvis.shape;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import setvis.SetOutline;

/**
 * Generates a {@link Shape} for the vertices generated by
 * {@link SetOutline#createOutline(Rectangle2D[], Rectangle2D[])}.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public abstract class AbstractShapeCreator {

	/**
	 * The generator for the vertices of the sets.
	 */
	private final SetOutline setOutline;

	/**
	 * Creates an {@link AbstractShapeCreator} with a given set outline creator.
	 * 
	 * @param setOutline
	 *            The creator of the set outlines.
	 */
	public AbstractShapeCreator(final SetOutline setOutline) {
		this.setOutline = setOutline;
	}

	/**
	 * The radius that should be added to the rectangles for the outline
	 * creation.
	 */
	private double radius = 10.0;

	/**
	 * @param radius
	 *            Sets the radius that should be added to the rectangles for the
	 *            outline creation.
	 */
	public void setRadius(final double radius) {
		this.radius = radius;
	}

	/**
	 * @return The radius which is added to the rectangles for the outline
	 *         creation.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Creates shapes for all sets given by {@code items}.
	 * 
	 * @param items
	 *            A list of sets. The sets are themselves a list of rectangles.
	 * @return The outline shapes for each set given.
	 */
	public Shape[] createShapesForLists(final List<List<Rectangle2D>> items) {
		final List<Rectangle2D[]> list = new LinkedList<Rectangle2D[]>();
		for (final List<Rectangle2D> group : items) {
			list.add(group.toArray(new Rectangle2D[group.size()]));
		}
		return createShapesFor(list);
	}

	/**
	 * Creates shapes for all sets given by {@code items}.
	 * 
	 * @param items
	 *            A list of sets. The sets are themselves an array of
	 *            rectangles.
	 * @return The outline shapes for each set given.
	 */
	public Shape[] createShapesFor(final List<Rectangle2D[]> items) {
		final Shape[] res = new Shape[items.size()];
		int i = 0;
		for (final Rectangle2D[] group : items) {
			res[i++] = createShapeFor(group, getNonMembers(items, i));
		}
		return res;
	}

	/**
	 * Finds all items not belonging to the given group.
	 * 
	 * @param items
	 *            A list of sets. The sets are themselves a list of rectangles.
	 * @param groupID
	 *            The group.
	 * @return All items not belonging to the group.
	 */
	private Rectangle2D[] getNonMembers(final List<Rectangle2D[]> items,
			final int groupID) {
		final List<Rectangle2D> res = new LinkedList<Rectangle2D>();
		int g = 0;
		for (final Rectangle2D[] group : items) {
			if (g++ == groupID) {
				continue;
			}
			for (final Rectangle2D r : group) {
				res.add(r);
			}
		}
		return res.toArray(new Rectangle2D[res.size()]);
	}

	/**
	 * Creates a shape for the given set avoiding the given items not contained
	 * in the set.
	 * 
	 * @param members
	 *            The items representing the set.
	 * @param nonMembers
	 *            The items excluded from the set.
	 * @return The resulting shape.
	 */
	public Shape createShapeFor(final Rectangle2D[] members,
			final Rectangle2D[] nonMembers) {
		final Rectangle2D[] m = mapRects(members);
		final Rectangle2D[] n = mapRects(nonMembers);
		final Point2D[] res = setOutline.createOutline(m, n);
		return convertToShape(res);
	}

	/**
	 * Maps rectangles by performing {@link #mapRect(Rectangle2D)} on each
	 * element of the array.
	 * 
	 * @param rects
	 *            The array to map.
	 * @return A new array containing the mapped rectangles.
	 */
	protected Rectangle2D[] mapRects(final Rectangle2D[] rects) {
		int i = rects.length;
		final Rectangle2D[] res = new Rectangle2D[i];
		while (--i >= 0) {
			res[i] = mapRect(rects[i]);
		}
		return res;
	}

	/**
	 * Maps one rectangle. The current map is just the identity but it can be
	 * changed by overwriting it in subclasses.
	 * 
	 * @param r
	 *            The rectangle to map.
	 * @return The new mapped rectangle. This method has to guarantee that the
	 *         returned rectangle is newly created.
	 */
	protected Rectangle2D mapRect(final Rectangle2D r) {
		return new Rectangle2D.Double(r.getMinX(), r.getMinY(), r.getWidth(), r
				.getHeight());
	}

	/**
	 * Converts vertices to a shape.
	 * 
	 * @param points
	 *            The sorted vertices representing the outlines of a set.
	 * @return The resulting shape.
	 */
	protected abstract Shape convertToShape(Point2D[] points);

}
