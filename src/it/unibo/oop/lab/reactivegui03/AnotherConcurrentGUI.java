package it.unibo.oop.lab.reactivegui03;

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

public final class AnotherConcurrentGUI extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = -8630968055862320453L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;

    private final JLabel counterLabel;
    private final JButton bUp;
    private final JButton bDown;
    private final JButton bStop;
    public AnotherConcurrentGUI() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());
        counterLabel = new JLabel();
        counterLabel.setText("0");
        mainPanel.add(counterLabel);
        bUp = new JButton("up");
        mainPanel.add(bUp);
        bDown = new JButton("down");
        mainPanel.add(bDown);
        bStop = new JButton("stop");
        mainPanel.add(bStop);
        /*
         * agent created (counter)
         */
        final Agent agent = new Agent();
        new Thread(agent).start();
        /*
         * agent stopper (counter)
         */
        final AgentStopper agentStopper = new AgentStopper(agent);
        new Thread(agentStopper).start();
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
        private volatile int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            AnotherConcurrentGUI.this.counterLabel.setText(Integer.toString(Agent.this.counter));
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

    private class AgentStopper implements Runnable {
        private final Agent agente;

        public AgentStopper(final Agent agente) {
            this.agente = agente;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(10_000);
                agente.stopCounting();
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            AnotherConcurrentGUI.this.bUp.setEnabled(false);
                            AnotherConcurrentGUI.this.bDown.setEnabled(false);
                            AnotherConcurrentGUI.this.bStop.setEnabled(false);
                        }
                    });
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
