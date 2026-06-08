package com.hangman.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Custom Canvas that draws the hangman figure progressively.
 *
 * Drawing stages (0 = empty gallows → maxErrors = full figure):
 * Each error adds one body part to the hanging figure.
 *
 * The canvas is declared in FXML with fx:type="com.hangman.view.HangmanCanvas".
 */
public class HangmanCanvas extends Canvas {

    private static final Color BG_TOP    = Color.web("#1a1a2e");
    private static final Color BG_BOT    = Color.web("#16213e");
    private static final Color GALLOWS_C = Color.web("#a0a0c0");
    private static final Color BODY_C    = Color.web("#e94560");
    private static final Color WIN_C     = Color.web("#2ecc71");

    public HangmanCanvas() {
        super(300, 340);
    }

    // JavaFX FXML requires a no-arg constructor — provided above.

    /**
     * Redraws the canvas for the given error count.
     *
     * @param errors    current number of wrong guesses
     * @param maxErrors total errors allowed (used to scale drawing steps)
     */
    public void draw(int errors, int maxErrors) {
        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();

        clearCanvas(gc, w, h);
        drawGallows(gc, w, h);

        // Map errors → body parts to draw (always 8 parts regardless of difficulty)
        // We scale: body part i is drawn when errors >= ceil(i * maxErrors / 8)
        int parts = 8; // total body parts
        for (int i = 1; i <= parts; i++) {
            double threshold = Math.ceil((double) i * maxErrors / parts);
            if (errors >= threshold) {
                drawBodyPart(gc, i, w, h);
            }
        }
    }

    /**
     * Draws the happy "WIN" animation — green figure with arms up.
     */
    public void drawWin() {
        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();

        clearCanvas(gc, w, h);
        drawGallows(gc, w, h);

        // Draw all parts in green
        gc.setStroke(WIN_C);
        gc.setLineWidth(4);
        double cx = w * 0.55, gy = h * 0.20;

        // Head
        double headR = 22;
        gc.strokeOval(cx - headR, gy + 5, headR * 2, headR * 2);

        // Smile
        gc.setLineWidth(2);
        gc.strokeArc(cx - 10, gy + 18, 20, 14, 200, 140, javafx.scene.shape.ArcType.OPEN);

        // Eyes
        gc.fillOval(cx - 8, gy + 12, 5, 5);
        gc.fillOval(cx + 3, gy + 12, 5, 5);

        gc.setLineWidth(4);
        // Body
        double bodyTop = gy + headR * 2 + 5;
        double bodyBot = bodyTop + 60;
        gc.strokeLine(cx, bodyTop, cx, bodyBot);

        // Arms up (victory pose)
        gc.strokeLine(cx, bodyTop + 20, cx - 35, bodyTop - 15);
        gc.strokeLine(cx, bodyTop + 20, cx + 35, bodyTop - 15);

        // Legs
        gc.strokeLine(cx, bodyBot, cx - 25, bodyBot + 40);
        gc.strokeLine(cx, bodyBot, cx + 25, bodyBot + 40);

        // WIN text
        gc.setFill(WIN_C);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("🏆 WIN!", w / 2, h - 15);
    }

    // ── Private helpers ──────────────────────────────────────────────────────────

    private void clearCanvas(GraphicsContext gc, double w, double h) {
        LinearGradient grad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, BG_TOP), new Stop(1, BG_BOT));
        gc.setFill(grad);
        gc.fillRect(0, 0, w, h);
    }

    private void drawGallows(GraphicsContext gc, double w, double h) {
        gc.setStroke(GALLOWS_C);
        gc.setLineWidth(5);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        double baseY  = h * 0.92;
        double baseX1 = w * 0.10, baseX2 = w * 0.90;
        double poleX  = w * 0.25;
        double poleTop= h * 0.08;
        double beamX2 = w * 0.58;
        double ropeBot= poleTop + h * 0.14;

        // Base
        gc.strokeLine(baseX1, baseY, baseX2, baseY);
        // Vertical pole
        gc.strokeLine(poleX, baseY, poleX, poleTop);
        // Horizontal beam
        gc.strokeLine(poleX, poleTop, beamX2, poleTop);
        // Short rope
        gc.setLineWidth(3);
        gc.strokeLine(beamX2, poleTop, beamX2, ropeBot);
        // Support diagonal
        gc.setLineWidth(4);
        gc.strokeLine(poleX, poleTop + 30, poleX + 35, poleTop);
    }

    /**
     * Draws one of 8 body parts. Parts are added in order:
     * 1=head, 2=body, 3=left arm, 4=right arm, 5=left leg, 6=right leg,
     * 7=left foot, 8=right foot
     */
    private void drawBodyPart(GraphicsContext gc, int part, double w, double h) {
        gc.setStroke(BODY_C);
        gc.setFill(BODY_C);
        gc.setLineWidth(4);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        double cx   = w * 0.58;
        double ropeBot = h * 0.08 + h * 0.14;
        double headR   = 22;
        double headTop = ropeBot;
        double headBot = headTop + headR * 2;
        double bodyBot = headBot + 60;

        switch (part) {
            case 1 -> { // Head
                gc.setLineWidth(3.5);
                gc.strokeOval(cx - headR, headTop, headR * 2, headR * 2);
                // Simple face: X eyes
                gc.setLineWidth(2);
                gc.strokeLine(cx - 9, headTop + 10, cx - 5, headTop + 14);
                gc.strokeLine(cx - 5, headTop + 10, cx - 9, headTop + 14);
                gc.strokeLine(cx + 5, headTop + 10, cx + 9, headTop + 14);
                gc.strokeLine(cx + 9, headTop + 10, cx + 5, headTop + 14);
            }
            case 2 -> // Body
                gc.strokeLine(cx, headBot, cx, bodyBot);
            case 3 -> // Left arm
                gc.strokeLine(cx, headBot + 18, cx - 30, headBot + 45);
            case 4 -> // Right arm
                gc.strokeLine(cx, headBot + 18, cx + 30, headBot + 45);
            case 5 -> // Left leg
                gc.strokeLine(cx, bodyBot, cx - 28, bodyBot + 42);
            case 6 -> // Right leg
                gc.strokeLine(cx, bodyBot, cx + 28, bodyBot + 42);
            case 7 -> { // Left foot
                gc.strokeLine(cx - 28, bodyBot + 42, cx - 42, bodyBot + 38);
            }
            case 8 -> { // Right foot
                gc.strokeLine(cx + 28, bodyBot + 42, cx + 42, bodyBot + 38);
            }
        }
    }
}
