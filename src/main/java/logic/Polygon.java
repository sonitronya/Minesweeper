package logic;

import UI.Graphics;
import UI.Main;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;


public class Polygon {

    private static final int numberOfSide = 6;
    private static final int countBomb = Main.countBomb;
    private static int currentCountCheckBomb = 0;
    private static List<Polygon> polygons = new ArrayList<>();
    private List<Point> points = new ArrayList<>();
    private PolygonType polygonType;
    private Point center;

    public Polygon(PolygonType polygonType, int radius, Point center) {
        this.polygonType = polygonType;
        this.center = center;
        createPoints(radius, center);
        polygons.add(this);
    }

    private void createPoints(int radius, Point center) {
        int a = 0;

        for (int i = 1; i < numberOfSide * 2 + 2; i++) {
            if (i % 2 == 0) {
                Point point = new Point(center.getX() + radius * cos(a * PI / 180),
                        center.getY() - radius * sin(a * PI / 180));
                points.add(point);
            }
            a += 180 / numberOfSide;
        }
    }

    public double[] getAllX() {
        double[] result = new double[numberOfSide];
        for (int i = 0; i < numberOfSide; i++) result[i] = points.get(i).getX();
        return result;
    }

    public double[] getAllY() {
        double[] result = new double[numberOfSide];
        for (int i = 0; i < numberOfSide; i++) result[i] = points.get(i).getY();
        return result;
    }

    private int getNumberOfBombNeardy() {

        return (int) getAdjacentPolygons().stream().filter(polygon ->
                polygon.polygonType == PolygonType.HIDDEN_BOMB).count();
    }

    private List<Polygon> getAdjacentPolygons() {
        List<Polygon> adjacentPolygons = new ArrayList<>();
        int polygonDiametr = Graphics.getPolygonDiametr();
        Point[] pointsForCheck = new Point[numberOfSide];

        pointsForCheck[0] = new Point(center.getX() + polygonDiametr / 2, center.getY() + polygonDiametr);
        pointsForCheck[1] = new Point(center.getX() + polygonDiametr / 2, center.getY() - polygonDiametr);
        pointsForCheck[2] = new Point(center.getX() - polygonDiametr / 2, center.getY() - polygonDiametr);
        pointsForCheck[3] = new Point(center.getX() - polygonDiametr / 2, center.getY() + polygonDiametr);
        pointsForCheck[4] = new Point(center.getX() + polygonDiametr, center.getY());
        pointsForCheck[5] = new Point(center.getX() - polygonDiametr, center.getY());



        Arrays.stream(pointsForCheck).forEachOrdered(point ->
                polygons.stream().filter(polygon ->
                        inPoly(point.getX(), point.getY(), polygon)).forEachOrdered(adjacentPolygons::add));
        return adjacentPolygons;
    }

    public static int getNumberOfSide() {
        return numberOfSide;
    }

    public static void click(MouseEvent event, Graphics graphics) {

        Polygon polygonOfClick = polygons.stream().filter(polygon ->
                inPoly(event.getX(), event.getY(), polygon)).findFirst().orElse(null);
        if (polygonOfClick == null) return;

        if (event.getButton() == MouseButton.SECONDARY) {
            checkBomb(polygonOfClick, graphics);
            return;
        }

        openPolygon(polygonOfClick, graphics);
    }

    private static void checkBomb(Polygon polygon, Graphics graphics) {

        graphics.fillPolygon(polygon.center.getX(), polygon.center.getY(), PolygonType.MARKED);
        if (polygon.polygonType == PolygonType.HIDDEN_BOMB) {
            currentCountCheckBomb++;
            if (currentCountCheckBomb == countBomb) {
                Main.gameOver(false);
            }
        } else if (polygon.polygonType == PolygonType.MARKED) {
            graphics.fillPolygon(polygon.center.getX(), polygon.center.getY(), PolygonType.CLOSED);
        }
    }

    private static void openPolygon(Polygon polygon, Graphics graphics) {
        if (polygon.polygonType == PolygonType.HIDDEN_BOMB) {
            polygon.polygonType = PolygonType.BOMB;
            graphics.fillPolygon(polygon.center.getX(), polygon.center.getY(), PolygonType.BOMB);
            polygons.clear();
            Main.gameOver(true);
        } else {
            int numberOfBomb = polygon.getNumberOfBombNeardy();
            polygon.polygonType = PolygonType.OPEN;
            if (numberOfBomb == 0) {
                graphics.fillPolygon(polygon.center.getX(), polygon.center.getY(), PolygonType.OPEN);
                polygon.openCellsAround(graphics);
            } else {
                graphics.fillPolygon(polygon.center.getX(), polygon.center.getY(), numberOfBomb);
            }
        }
    }

    private void openCellsAround(Graphics graphics) {
        List<Polygon> polygonsAround = getAdjacentPolygons();

        polygonsAround.stream().filter(polygon -> polygon.getNumberOfBombNeardy() == 0).forEachOrdered(polygon -> {
            polygon.polygonType = PolygonType.OPEN;
            graphics.fillPolygon(polygon.center.getX(), polygon.center.getY(), PolygonType.OPEN);
        });
    }

    private static boolean inPoly(double x, double y, Polygon polygon) {

        int j = getNumberOfSide() - 1;
        double[] xPol = polygon.getAllX();
        double[] yPol = polygon.getAllY();
        boolean c = false;
        for (int i = 0; i < getNumberOfSide(); i++) {
            if (((yPol[i] <= y && y < yPol[j]) || (yPol[j] <= y && y < yPol[i])) &&
                    (x > (xPol[j] - xPol[i]) * (y - yPol[i]) / (yPol[j] - yPol[i]) + xPol[i])) {
                c = !c;
            }
            j = i;
        }
        return c;
    }

    public void setPolygonType(PolygonType polygonType) {
        this.polygonType = polygonType;
    }

    public static List<Polygon> getPolygons() {
        return polygons;
    }

    @Override
    public String toString() {
        return center.toString();
    }
}
