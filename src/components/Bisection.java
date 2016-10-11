package components;



import java.applet.Applet;
import java.awt.*;
import info.lundin.math.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.TimeUnit;

public class Bisection extends Applet  implements Runnable {

    TextField input;
    TextField iter;
    TextField tolTxt;
    TextField txtA;
    TextField txtB;
    
    
    TextArea display;
    Button go;
    Button stop;
    Button contin;
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
    Label lblError;
    Label lblA;
    Label lblB;
    
    Derive d;
    Eval e;
    Thread t;
    BigDecimal a;
    BigDecimal b;
    BigDecimal x;
    BigDecimal funcVal;
    BigDecimal b_a;
    BigDecimal tol;
    BigDecimal f_b, f_a;
    
    BigDecimal fInitVal;
    
    final BigDecimal TWO=new BigDecimal("2");
    boolean loop = false;

    @Override
    public void init() {
       
        input = new TextField();
        
        txtA=new TextField();
        txtB=new TextField();
        
        iter = new TextField("10");
        tolTxt=new TextField();
        display = new TextArea(5, 30);
        go = new Button("Start");
        stop = new Button("Stop");
        contin = new Button("Continue");

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

        input.setBackground(Color.white);
        
        txtA.setBackground(Color.white);
        txtB.setBackground(Color.white);
        iter.setBackground(Color.white);
        display.setBackground(Color.white);

        lblEquation = new Label("Equation, f(x) = g(x)");
        
        lblA=new Label("Left border");
        lblB=new Label("Right border");        
        lblIterations = new Label("Number of iterations");
        lblError=new Label("Allowable error");
        setLayout(new BorderLayout());

        commonPanel.setLayout(new GridLayout(7, 1));
        btnPanel.setLayout(new GridLayout(1, 5)); //for btns
        lblPanel.setLayout(new GridLayout(1, 3)); //labels for startValue value and iterations
        txtPanel.setLayout(new GridLayout(1, 4)); //txtfields for startValue value , iterations, tolerance
        lblPanel2.setLayout(new GridLayout(1, 2)); //labels for startValue value and iterations
        txtPanel2.setLayout(new GridLayout(1, 2)); //txtfi
        
        lblPanel.add(lblA);
        lblPanel.add(lblB);
        lblPanel2.add(lblIterations);
        lblPanel2.add(lblError);
        txtPanel.add(txtA);
        txtPanel.add(txtB);
        txtPanel2.add(iter);
        txtPanel2.add(tolTxt);
        btnPanel.add(p4);
        btnPanel.add(go);
        btnPanel.add(stop);
        btnPanel.add(contin);
        btnPanel.add(p5);
        commonPanel.add(lblEquation);
        commonPanel.add(input);
        commonPanel.add(lblPanel);
        commonPanel.add(txtPanel);
        commonPanel.add(lblPanel2);
        commonPanel.add(txtPanel2);
        commonPanel.add(btnPanel);
        add("North", commonPanel);
        add("Center", display);
    }

    @Override
    public boolean action(Event event, Object obj) {
        if (event.target == go) {
            startThread();
            return true;
        }
        if (event.target == stop) {
            loop = false;
            return true;
        }
        if (event.target == contin) {
            txtA.setText(String.valueOf(a));
            txtB.setText(String.valueOf(b));
            startThread();
            return true;
        }

        return false;
    }

    public void startThread() {
        try {
            t = new Thread(this);
            t.start();
        } catch (Exception exception) {
            display.append("Error: this is a serious error, " + exception + "\n" + exception.getMessage());
        }
    }

    @Override
    public void stop() {
        loop = false;
        t = null;
    }

    @Override
    public void run() {
        boolean flag = false;
        int k = 0, i = 0, maxIter = 0;
        String s1,  derive;

        loop = true;
        go.setEnabled(false);
        contin.setEnabled(false);

        
        
        try {
            display.append("\n");
            s1 = input.getText().trim();

            if (s1.equals("")) {
                throw new Exception("No equation given");
            }
            long startTime = System.nanoTime(); 
            
            a = new BigDecimal(txtA.getText().trim());
            b = new BigDecimal(txtB.getText().trim());
            maxIter = Integer.parseInt(iter.getText().trim());
            tol=new BigDecimal(tolTxt.getText().trim());
            
            if ((i = s1.indexOf("=")) != -1) {
                s1 = s1.substring(0, i) + "-(" + s1.substring(i + 1, s1.length()) + ")";
            }

            
            Expression expression = new Expression(s1);
            
            f_a = expression.with("x",a).eval();
            f_b = expression.with("x",b).eval();
            
            
            for (; k < maxIter && loop; k++) {
                x=b.add(a).divide(TWO,MathContext.DECIMAL128);
                funcVal =expression.with("x", x).eval(); 
                if(fInitVal == null)
                    fInitVal=funcVal;
                
                display.append((k + 1) +". x= " + x + ";  f(x)= " + funcVal + ";  abs(b-a)= "+ (b.subtract(a)).abs() + "\n");
                
                if(f_a.multiply(funcVal).signum()==-1){ //negative
                    b=x;
                    f_b = funcVal;
                }else {
                    a = x;
                    f_a = funcVal;
                }
                
                if((b.subtract(a)).abs().compareTo(tol)==-1){
                    break;
                }
                
            }
            long estimatedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            
            if(k>=maxIter)
                display.append("Elapsed Time= " + estimatedTime + " ms.. Maximum iterations reached\n");
            else
                display.append("Elapsed Time= " + estimatedTime + " ms.\n");
            
        } catch (NumberFormatException _ex) {
            display.append("Error: please make sure  that a start value, the number of iterations \nand allowable error were given and are numbers \n\n");
        } catch (Exception exception) {
            display.append("Error: " + exception.getMessage() + "\n\n");
        }

        loop = false;
        contin.setEnabled(true);
        go.setEnabled(true);
    }

    public double getProgress(){
        return 100-funcVal.divide(fInitVal, MathContext.DECIMAL128).multiply(new BigDecimal("100")).intValue();
    }
}
