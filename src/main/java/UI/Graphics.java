package UI;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import logic.Point;
import logic.Polygon;
import logic.PolygonType;

public class Graphics {

    private static GraphicsContext gc;
    private static int POLYGON_RADIUS = 20;
    private static int POLYGON_DIAMETR = POLYGON_RADIUS * 2;

    Graphics(GraphicsContext graphicsContext, double height, double width) {
        gc = graphicsContext;
        fillStartField((int) height, (int) width);
    }

    private void fillStartField(int height, int width) {
        for (int x = POLYGON_RADIUS; x <= width - POLYGON_RADIUS; x += POLYGON_DIAMETR) {
            for (int y = POLYGON_RADIUS; y <= height - POLYGON_RADIUS; y += POLYGON_DIAMETR) {
                if ((y / POLYGON_DIAMETR) % 2 == 0) {
                    fillPolygon(x + POLYGON_RADIUS, y, PolygonType.CLOSED);
                } else {
                    fillPolygon(x, y, PolygonType.CLOSED);
                }
            }
        }
    }

    public void fillPolygon(double x, double y, PolygonType polygonType) {
        gc.setStroke(Color.GRAY);
        gc.setFill(getPolygonColor(polygonType));

        Polygon polygon = new Polygon(polygonType, POLYGON_RADIUS, new Point(x, y));

        gc.strokePolygon(polygon.getAllX(), polygon.getAllY(), Polygon.getNumberOfSide());
        gc.fillPolygon(polygon.getAllX(), polygon.getAllY(), Polygon.getNumberOfSide());
    }

    private Color getPolygonColor(PolygonType polygonType) {
        switch (polygonType) {
            case BOMB:
                return Color.RED;
            case OPEN:
                return Color.ALICEBLUE;
            case MARKED:
                return Color.DARKBLUE;
            default:
                return Color.CORNFLOWERBLUE;
        }
    }

    public void fillPolygon(double x, double y, int numberOfBomb) {
        fillPolygon(x, y, PolygonType.OPEN);
        gc.setFill(Color.BLACK);
        gc.setFont(Font.getDefault());
        gc.fillText(Integer.toString(numberOfBomb), x, y, 20);
    }

    public static int getPolygonDiametr() {
        return POLYGON_DIAMETR;
    }
}
