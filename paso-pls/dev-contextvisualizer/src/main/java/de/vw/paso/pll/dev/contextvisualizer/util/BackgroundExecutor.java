package de.vw.paso.pll.dev.contextvisualizer.util;

import de.vw.paso.pll.dev.contextvisualizer.util.ProcessMonitor;
import de.vw.paso.pll.dev.contextvisualizer.util.ProcessUpdateListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

public class BackgroundExecutor {

  public static class LoadingPane extends JPanel {

    private JLabel l;

    public LoadingPane() {
      setOpaque(true);
      setBackground(new Color(79, 79, 79, 50));
      addKeyListener(new KeyListener() {
        public void keyPressed(KeyEvent e) {
          e.consume();
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
          e.consume();
        }
      });
      addMouseListener(new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
          mouseEvent.consume();
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
          mouseEvent.consume();
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
          mouseEvent.consume();
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
          mouseEvent.consume();
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
          mouseEvent.consume();
        }
      });

      setLayout(new FlowLayout());
      add(Box.createGlue());
      l = new JLabel();
      l.setFont(new Font("Arial", Font.BOLD, 20));
      add(l, BorderLayout.CENTER);
      add(Box.createGlue());
    }

    public void setLoadingText(String text) {
      l.setText(text);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      setOpaque(true);
      super.paintComponent(graphics);
      setOpaque(false);
    }
  }

  private LoadingPane glassPane;

  public BackgroundExecutor(JFrame window) {
    glassPane = new LoadingPane();
    window.setGlassPane(glassPane);
  }

  public void doInBackground(String taskName, Consumer<ProcessMonitor> task) {
    Thread t = new Thread(() -> {
      ProcessMonitor processMonitor = new ProcessMonitor();
      processMonitor.addListener(new ProcessUpdateListener() {
        @Override
        public void currentStepChange(String step) {
          glassPane.setLoadingText(step);
        }
      });
      glassPane.setLoadingText(taskName);
      glassPane.setVisible(true);
      task.accept(processMonitor);
      glassPane.setVisible(false);
      glassPane.setLoadingText(null);
    });
    t.start();
  }
}
