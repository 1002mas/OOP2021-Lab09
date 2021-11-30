package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class ConcurrentGUI extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = -8630968055862320453L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;

    private final JLabel counterLabel;

    public ConcurrentGUI() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());
        counterLabel = new JLabel();
        counterLabel.setText("0");
        mainPanel.add(counterLabel);
        final JButton bUp = new JButton("up");
        mainPanel.add(bUp);
        final JButton bDown = new JButton("down");
        mainPanel.add(bDown);
        final JButton bStop = new JButton("stop");
        mainPanel.add(bStop);
        /*
         * agent created (counter)
         */
        final Agent agent = new Agent();
        new Thread(agent).start();
        /*
         * Action Listeners
         */
        bUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.setInc();
            }
        });
        bDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.setDec();
            }
        });
        bStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.stopCounting();
                bUp.setEnabled(false);
                bDown.setEnabled(false);
                bStop.setEnabled(false);
            }
        });
        this.setContentPane(mainPanel);
        this.setVisible(true);
    }

    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean dec;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final String textToDisplay = String.valueOf(counter);
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            ConcurrentGUI.this.counterLabel.setText(textToDisplay);
                        }
                    });
                    if (dec) {
                        this.counter--;
                    } else {
                        this.counter++;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        /**
         * External commands
         */
        public void setInc() {
            this.dec = false;
        }

        public void setDec() {
            this.dec = true;
        }

        public void stopCounting() {
            this.stop = true;
        }
    }
}
