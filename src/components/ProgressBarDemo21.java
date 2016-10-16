package components;

import info.lundin.math.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;

public class ProgressBarDemo21 extends JPanel implements ActionListener, PropertyChangeListener {

    private JProgressBar progressBar;
    private Task task;
    
    TextField txtFunction;
    TextField txtStartValue;
    TextField txtIter;
    TextField txtTol;
    TextField txtDerive;
    
    TextArea display;
    Button btnGo;
    Button btnStop;
    Button btnContin;
    Button btnReset;

    Panel commonPanel;
    Panel btnPanel;
    Panel lblPanel;
    Panel txtPanel;
    Panel p4;
    Panel p5;
    Label lblEquation;
    Label lblDerive;
    Label lblStartVal;
    Label lblIterations;
    Label lblTolerance;
    Derive d = new Derive();
    Apfloat xVal;
    Apfloat xPrev;
    Apfloat funcVal;
    Apfloat b_a;
    Apfloat tol;
    Apfloat a;
    Apfloat b;
    
    Eval e=new Eval();
    boolean loop = false;

    class Task extends SwingWorker<Void, IntermediateResult> {

        private TextArea display;

        Task(TextArea display) {
            this.display = display;
        }

        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {

            int k = 0, substringPos, maxIter;
            String functionStr, derive;

            loop = true;
            btnGo.setEnabled(false);
            btnContin.setEnabled(false);

            try {
                display.append("\n");
                functionStr = txtFunction.getText().trim();

                if (functionStr.equals("")) {
                    throw new Exception("No equation given");
                }
                int progress;
                //Initialize progress property.
                setProgress(0);
                xVal = new Apfloat(txtStartValue.getText().trim());
                tol = new Apfloat(txtTol.getText().trim());
                maxIter = Integer.parseInt(txtIter.getText().trim());

                if ((substringPos = functionStr.indexOf("=")) != -1) {
                    functionStr = functionStr.substring(0, substringPos) + "-(" + functionStr.substring(substringPos + 1, functionStr.length()) + ")";
                }

                long startTime = System.nanoTime();

                derive = d.diff(functionStr, "x")[0];
                txtDerive.setText(derive);
                
                String strTol=tol.toString(true);
                long precision=strTol.length()-1-strTol.indexOf('.');
                FixedPrecisionApfloatHelper helper=new FixedPrecisionApfloatHelper(precision);
                
                Expression expression=null, deriveExpression=null;
                try{
                    expression = new Expression(functionStr, precision);
                    deriveExpression = new Expression(derive, precision); //derive
                }catch(Exception e){
                    e.printStackTrace();
                }
                
                funcVal = expression.with("x", xVal).eval();
                Apfloat temp, deriveVal;
                for (; k < maxIter && loop; k++) {
                    xPrev = xVal;
                    deriveVal=deriveExpression.with("x", xVal).eval();
                    temp=helper.divide(funcVal, deriveVal);
                    xVal =helper.subtract(xVal, temp);  //xVal - e.eval(s1, "x=" + xVal) / e.eval(derive, "x=" + xVal);
                    funcVal = expression.with("x", xVal).eval();
                    //interacts with GUI
//                  ========================================================
                    progress = (int) (k + 1) * 100 / maxIter;
                    setProgress(progress);
//                    publish(new IntermediateResult(k + 1, xVal, funcVal, (xPrev.subtract(xVal)).abs()));
                    display.append((k + 1) + ". x= " + xVal + ";  f(x)= " + funcVal +" f'(x) = " + deriveVal +   ";  abs(b-a)= " + ApfloatMath.abs(xPrev.subtract(xVal)).toString() + "\n"); //toEngineeringString toString
//                  ========================================================
                   
                    if ( helper.abs(helper.subtract(xPrev,xVal)).compareTo(tol) == -1) {
                        break;
                    }

                }
                setProgress(100);
                long estimatedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

                if (k >= maxIter) {
                    display.append("Elapsed Time= " + estimatedTime + " ms. Maximum iterations reached\n");
                } else {
                    display.append("Elapsed Time= " + estimatedTime + " ms.\n");
                }

//            } catch (NumberFormatException _ex) {
//                display.append("Error: please make sure  that a start value, the number of iterations \nand tolerance were given and are numbers \n\n");
            } catch (Exception exception) {
                display.append("Error: " + exception.getMessage() + "\n\n");
            }

            loop = false;

            return null;
        }

        @Override
        protected void process(java.util.List<IntermediateResult> resultList) {
            for (IntermediateResult result : resultList) {
//                    display.append((k + 1) + ". x= " + xVal + ";  f(x)= " + funcVal + ";  abs(b-a)= " + (xPrev.subtract(xVal)).abs().toString() + "\n");
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
//            taskOutput.append("Done!\n");
        }
    } //end class

    public ProgressBarDemo21() {

        super(new BorderLayout());
        txtFunction = new TextField("x^2-4*sin(x)");
        txtStartValue = new TextField("3");
        txtIter = new TextField("1000");
        txtTol = new TextField("1e-28");
        txtDerive=new TextField();
        txtDerive.setEditable(false);
        display = new TextArea(5, 30);

        btnGo = new Button("Start");
        btnStop = new Button("Stop");
        btnContin = new Button("Continue");
        btnReset = new Button("Clear");

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
        p5 = new Panel();

        commonPanel.setBackground(Color.lightGray);
        btnPanel.setBackground(Color.lightGray);
        lblPanel.setBackground(Color.lightGray);
        txtPanel.setBackground(Color.lightGray);
        p4.setBackground(Color.lightGray);
        p5.setBackground(Color.lightGray);

        txtFunction.setBackground(Color.white);
        txtStartValue.setBackground(Color.white);
        txtIter.setBackground(Color.white);
        display.setBackground(Color.white);

        lblEquation = new Label("Equation, f(x) = g(x)");
        lblDerive=new Label("Derivation");
        lblStartVal = new Label("Start value");
        lblIterations = new Label("Number of iterations");
        lblTolerance = new Label("Tolerance");

        commonPanel.setLayout(new GridLayout(7, 1));
        btnPanel.setLayout(new GridLayout(1, 5)); //for btns
        lblPanel.setLayout(new GridLayout(1, 3)); //labels for startValue value and iterations
        txtPanel.setLayout(new GridLayout(1, 4)); //txtfields for startValue value , iterations, tolerance

        lblPanel.add(lblStartVal);
        lblPanel.add(lblIterations);
        lblPanel.add(lblTolerance);
        txtPanel.add(txtStartValue);
        txtPanel.add(txtIter);
        txtPanel.add(txtTol);
        btnPanel.add(p4);
        btnPanel.add(btnGo);
        btnPanel.add(btnStop);
        btnPanel.add(btnContin);
        btnPanel.add(btnReset);

        btnPanel.add(p5);
        commonPanel.add(lblEquation);
        commonPanel.add(txtFunction);
        commonPanel.add(lblDerive);
        commonPanel.add(txtDerive);
        commonPanel.add(lblPanel);
        commonPanel.add(txtPanel);
        commonPanel.add(btnPanel);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        add(commonPanel, BorderLayout.PAGE_START);
        add(new JScrollPane(display), BorderLayout.CENTER);
        add(progressBar, BorderLayout.PAGE_END);
    }

    /**
     * Invoked when the user presses the start button.
     *
     */
    public void actionPerformed(ActionEvent evt) {

        if ("go".equals(evt.getActionCommand())) {

//            progressBar.setIndeterminate(true);
            //Instances of javax.swing.SwingWorker are not reusuable, so
            //we create new instances as needed.
            
            task = new Task(display);
            task.addPropertyChangeListener(this);
            task.execute();
            

        } else if ("stop".equals(evt.getActionCommand())) {
            loop = false;
        } else if ("continue".equals(evt.getActionCommand())) {
            txtStartValue.setText(String.valueOf(xVal));
            task = new Task(display);
            task.addPropertyChangeListener(this);
            task.execute();
        } else if ("reset".equals(evt.getActionCommand())) {
            display.setText("" + '\u0000'); //ACSII code of 0 is '\u0000'
//            txtFunction.setText("" + '\u0000');
//            txtTol.setText("" + '\u0000');
//            txtStartValue.setText("" + '\u0000');
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
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Newton Method");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new ProgressBarDemo21();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        
        frame.setSize(new Dimension(1200, 400));
        frame.setLocationRelativeTo(null);
//        frame.pack();
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
        
        System.out.println(Math.PI);
    }
}
