
package components;

import info.lundin.math.Derive;
import info.lundin.math.Eval;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import java.util.List;
public class ProgressBarDemo2 extends JPanel   implements ActionListener, PropertyChangeListener {

    private JProgressBar progressBar;
    private Task task;

    TextField txtFunction;
//    JComboBox<Object>
    TextField txtIter;
    TextField txtTol;
    TextField txtA;
    TextField txtB;

    TextArea display;
    
    Button btnGo;
    Button btnStop;
    Button btnContin;
    Button btnReset;
    
    Panel commonPanel;
    Panel btnPanel;
    Panel lblPanel;
    Panel txtPanel;
    Panel lblPanel2;
    Panel txtPanel2;

    Panel p4;
    Panel p5;
    Label lblEquation;
    Label lblIterations;
    Label lblTolerance;
    Label lblA;
    Label lblB;
    
    BigDecimal a;
    BigDecimal b;
    BigDecimal f_b, f_a;
    BigDecimal x;
    BigDecimal funcVal;
    BigDecimal b_a;
    BigDecimal tol;
    

    final BigDecimal TWO = new BigDecimal("2");
    boolean loop = false;

    class Task extends SwingWorker<Void, IntermediateResult> {
        
        private TextArea display;
        
        Task(TextArea display){
            this.display=display;
        }
        
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
           
            int k = 0, substringPos,  maxIter ;
            String functionStr;

            loop = true;
            btnGo.setEnabled(false);
            btnContin.setEnabled(false);

            try {
                display.append("\n");
                functionStr = txtFunction.getText().trim();

                if (functionStr.equals("")) {
                    throw new Exception("No equation given");
                }
                long startTime = System.nanoTime();
                int progress;
                //Initialize progress property.
                setProgress(0);
                a = new BigDecimal(txtA.getText().trim());
                b = new BigDecimal(txtB.getText().trim());
                maxIter = Integer.parseInt(txtIter.getText().trim());
                tol = new BigDecimal(txtTol.getText().trim());

                if ((substringPos = functionStr.indexOf("=")) != -1) {
                    functionStr = functionStr.substring(0, substringPos) + "-(" + functionStr.substring(substringPos + 1, functionStr.length()) + ")";
                }

                Expression expression = new Expression(functionStr);

                f_a = expression.with("x", a).eval();
                f_b = expression.with("x", b).eval();

                for (; k < maxIter && loop && (b.subtract(a)).abs().compareTo(tol) == 1; k++) {
                    
                    x = b.add(a).divide(TWO, MathContext.DECIMAL128);
                    funcVal = expression.with("x", x).eval();
                    
                    //interacts with GUI
//                  ========================================================
                    progress=(int) (k+1)*100/maxIter;
                    setProgress(progress);
//                    publish(new IntermediateResult(k+1, x, funcVal, (b.subtract(a)).abs()));
                display.append((k + 1) + ". x= " + x + ";  f(x)= " + funcVal + ";  abs(b-a)= " + (b.subtract(a)).abs().toEngineeringString()+ "\n"); //setScale(4, RoundingMode.HALF_UP) toString

//                  =========================================================
                    if (f_a.multiply(funcVal).signum() == -1) { //f(a) and f(x) are on the opposite sides of Y-axis
                        b = x;
                        f_b = funcVal;
                    } else {
                        a = x;
                        f_a = funcVal;
                    }

//                    if ((b.subtract(a)).abs().compareTo(tol) == -1) {
//                        break;
//                    }

                }
                setProgress(100);
                long estimatedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

                if (k >= maxIter) {
                    display.append("Elapsed Time= " + estimatedTime + " ms.. Maximum iterations reached\n");
                } else {
                    display.append("Elapsed Time= " + estimatedTime + " ms.\n");
                }

            }catch (NumberFormatException _ex) {
                display.append("Error: please make sure  that border values, the number of iterations \nand tolerance were given and are numbers \n\n");
            } catch (Exception exception) {
                display.append("Error: " + exception.getMessage() + "\n\n");
            }

            loop = false;
            

            return null;
        }
        
        
        @Override
        protected  void process(List<IntermediateResult> resultList){
             for (IntermediateResult result : resultList){
//                display.append((k + 1) + ". x= " + x + ";  f(x)= " + funcVal + ";  abs(b-a)= " + (b.subtract(a)).abs().toString() + "\n");
                display.append(result.iter + ". x= " + result.x + ";  f(x)= " + result.funcVal + ";  abs(b-a)= " + result.b_a.toString() + "\n");
    
            }
        }
        
        /*
         * Executed in event dispatch thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            btnContin.setEnabled(true);
            btnGo.setEnabled(true);
        }
    }//end class

    public ProgressBarDemo2() {
        super(new BorderLayout());
        txtFunction = new TextField();

        txtA = new TextField();
        txtB = new TextField();

        txtIter = new TextField("100");
        txtTol = new TextField("1e-28");
        display = new TextArea(5, 30);
        btnGo = new Button("Start");
        btnStop = new Button("Stop");
        btnContin = new Button("Continue");
        btnReset= new Button("Clear");
        
        btnGo.setActionCommand("go");
        btnStop.setActionCommand("stop");
        btnContin.setActionCommand("continue");
        btnReset.setActionCommand("reset");
        
        btnGo.addActionListener(this);
        btnStop.addActionListener(this);
        btnContin.addActionListener(this);
        btnReset.addActionListener(this);
        
        commonPanel = new Panel();
        btnPanel = new Panel();
        p4 = new Panel();
        lblPanel = new Panel();
        txtPanel = new Panel();
        lblPanel2 = new Panel();
        txtPanel2 = new Panel();
        p5 = new Panel();

        commonPanel.setBackground(Color.lightGray);
        btnPanel.setBackground(Color.lightGray);
        lblPanel.setBackground(Color.lightGray);
        txtPanel.setBackground(Color.lightGray);
        lblPanel2.setBackground(Color.lightGray);
        txtPanel2.setBackground(Color.lightGray);
        p4.setBackground(Color.lightGray);
        p5.setBackground(Color.lightGray);

        txtFunction.setBackground(Color.white);

        txtA.setBackground(Color.white);
        txtB.setBackground(Color.white);
        txtIter.setBackground(Color.white);
        display.setBackground(Color.white);

        lblEquation = new Label("Equation, f(x) = g(x)");

        lblA = new Label("Left border");
        lblB = new Label("Right border");
        lblIterations = new Label("Number of iterations");
        lblTolerance = new Label("Tolerance");

        commonPanel.setLayout(new GridLayout(7, 1));
        btnPanel.setLayout(new GridLayout(1, 5)); //for btns
        lblPanel.setLayout(new GridLayout(1, 3)); //labels for startValue value and iterations
        txtPanel.setLayout(new GridLayout(1, 4)); //txtfields for startValue value , iterations, tolerance
        lblPanel2.setLayout(new GridLayout(1, 2)); //labels for startValue value and iterations
        txtPanel2.setLayout(new GridLayout(1, 2)); //txtfi

        lblPanel.add(lblA);
        lblPanel.add(lblB);
        lblPanel2.add(lblIterations);
        lblPanel2.add(lblTolerance);
        txtPanel.add(txtA);
        txtPanel.add(txtB);
        txtPanel2.add(txtIter);
        txtPanel2.add(txtTol);
        btnPanel.add(p4);
        btnPanel.add(btnGo);
        btnPanel.add(btnStop);
        btnPanel.add(btnContin);
        btnPanel.add(btnReset);
        btnPanel.add(p5);
        
        commonPanel.add(lblEquation);
        commonPanel.add(txtFunction);
        commonPanel.add(lblPanel);
        commonPanel.add(txtPanel);
        commonPanel.add(lblPanel2);
        commonPanel.add(txtPanel2);
        commonPanel.add(btnPanel);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        
        add( commonPanel, BorderLayout.PAGE_START);
        add( display, BorderLayout.CENTER);
        add( progressBar, BorderLayout.PAGE_END);
    }

    /**
     * Invoked when the user presses the start button.
     * @param evt
     */
    public void actionPerformed(ActionEvent evt) {

        if ("go".equals(evt.getActionCommand())) {

//            progressBar.setIndeterminate(true);
            task = new Task(display);
            task.addPropertyChangeListener(this);
            task.execute();
            
        } else if ("stop".equals(evt.getActionCommand())) {
            loop = false;
        } else if ("continue".equals(evt.getActionCommand())) {
            txtA.setText(String.valueOf(a));
            txtB.setText(String.valueOf(b));
//            progressBar.setIndeterminate(true);
            //Instances of javax.swing.SwingWorker are not reusuable, so
            //we create new instances as needed.
            task = new Task(display);
            task.addPropertyChangeListener(this);
            task.execute();
        }else if ("reset".equals(evt.getActionCommand())) {
//            txtA.setText(""+'\u0000');
//            txtB.setText(""+'\u0000');
//            txtFunction.setText(""+'\u0000');
//            txtTol.setText(""+'\u0000');
            display.setText(""+'\u0000');
                    
        }

    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setIndeterminate(false);
            progressBar.setValue(progress);
        }
    }

    /**
     * Create the GUI and show it. As with all GUI code, this must run on the
     * event-dispatching thread.
     */
    private static  void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Bisection Method");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Create and set up the content pane.
        JComponent newContentPane = new ProgressBarDemo2();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        
        frame.setSize(1200, 400);
        frame.setLocationRelativeTo(null);
//        frame.pack();
//        frame.set
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
