package org.kabeja.svg.generators;

import java.util.Map;

import org.kabeja.dxf.DXFBlock;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFInsert;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SVGInsertGenerator extends AbstractSVGSAXGenerator{

	public void toSAX(ContentHandler handler, Map svgContext, DXFEntity entity,
			TransformContext transformContext) throws SAXException {
	   
		DXFInsert insert =(DXFInsert)entity;
		
		DXFBlock block = insert.getDXFDocument().getDXFBlock(insert.getBlockID());

        StringBuffer buf = new StringBuffer();

        Point bp = block.getReferencePoint();

        int rows = insert.getRows();
        int columns = insert.getColumns();
        double rotate = insert.getRotate();
        Point p = insert.getPoint();
        double scale_x = insert.getScaleX();
        double scale_y = insert.getScaleY();
        double column_spacing = insert.getColumnSpacing();
        double row_spacing = insert.getRowSpacing();
        
        // translate to the insert point all the rows and columns
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                // translate to the insert point
                buf.append("translate(");
                buf.append(SVGUtils.formatNumberAttribute((p.getX() - (column_spacing * column))));
                buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                buf.append(SVGUtils.formatNumberAttribute((p.getY() - (row_spacing * row))));
                buf.append(")");

                // then rotate
                if (rotate != 0) {
                    buf.append(" rotate(");
                    buf.append(SVGUtils.formatNumberAttribute(rotate));
                    buf.append(")");
                }

                // then scale
                if ((scale_x != 1.0) || (scale_y != 1.0)) {
                    buf.append(" scale(");
                    buf.append(SVGUtils.formatNumberAttribute(scale_x));
                    buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    buf.append(SVGUtils.formatNumberAttribute(scale_y));
                    buf.append(")");
                }

                if ((bp.getX() != 0.0) || (bp.getY() != 0.0)) {
                    buf.append(" translate(");
                    buf.append(SVGUtils.formatNumberAttribute(bp.getX()));
                    buf.append(SVGConstants.SVG_ATTRIBUTE_PATH_PLACEHOLDER);
                    buf.append(SVGUtils.formatNumberAttribute(bp.getY()));
                    buf.append(")");
                }

                AttributesImpl attr = new AttributesImpl();
                SVGUtils.addAttribute(attr, "transform", buf.toString());

                // add common attributes
                super.setCommonAttributes(attr, svgContext,insert);

                // fix the scale of stroke-width
                if ((scale_x + scale_y) > 0) {
                    double width = 0.04 / (scale_x +scale_y);
                    SVGUtils.addAttribute(attr, "stroke-width",
                         SVGUtils.formatNumberAttribute(width) + "%");
                }

                // SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);
                // attr = new AttributesImpl();
                attr.addAttribute(SVGConstants.XMLNS_NAMESPACE, "xlink", "xmlns:xlink", "CDATA",
                    SVGConstants.XLINK_NAMESPACE);

                attr.addAttribute(SVGConstants.XLINK_NAMESPACE, "href",
                    "xlink:href", "CDATA",
                    "#" + SVGUtils.validateID(insert.getBlockID()));

                SVGUtils.emptyElement(handler, SVGConstants.SVG_USE, attr);

                // SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
                buf.delete(0, buf.length());
            }
        }
		
	}

}