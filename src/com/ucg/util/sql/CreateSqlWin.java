package com.ucg.util.sql;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

@SuppressWarnings("serial")  
public class CreateSqlWin extends JFrame {  
  
    private JPanel contentPane;  
    private JTextField txtStr;  
    private JRadioButton rdbtnStringbuffer;  
    private JSplitPane splitPane;  
    private JTextArea newSql;  
    private JTextArea oldSql;  
 
   
    private ImageIcon ico = new ImageIcon(this.getClass().getResource("/sql.png"));  
  
    /** 
     * Launch the application. 
     */  
    public static void main(String[] args) {  
        EventQueue.invokeLater(new Runnable() {  
            public void run() {  
                try {  
                    CreateSqlWin frame = new CreateSqlWin();  
                    frame.setVisible(true);  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        });  
    }  
  
    /** 
     * Create the frame. 
     */  
    public CreateSqlWin() {  
        setIconImage(ico.getImage());  
        setMinimumSize(new Dimension(840, 600));  
        setTitle("SQL和JAVA互转字符串");  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        setBounds(100, 100, 1000, 605);  
        contentPane = new JPanel();  
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));  
        setContentPane(contentPane);  
        contentPane.setLayout(new BorderLayout(0, 0));  
          
        JPanel panel = new JPanel();  
        panel.setPreferredSize(new Dimension(10, 80));  
        contentPane.add(panel, BorderLayout.NORTH);  
        panel.setLayout(new BorderLayout(0, 0));  
          
        JPanel panel_1 = new JPanel();  
        panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));  
        panel_1.setPreferredSize(new Dimension(300, 10));  
        panel.add(panel_1, BorderLayout.CENTER);  
        panel_1.setLayout(null);  
          
        JLabel label = new JLabel("选择生成方式：");  
        label.setBounds(10, 10, 153, 20);  
        panel_1.add(label);  
          
          
        rdbtnStringbuffer = new JRadioButton("StringBuffer");  
        rdbtnStringbuffer.setBounds(40, 36, 107, 23);  
        rdbtnStringbuffer.setSelected(true);
        panel_1.add(rdbtnStringbuffer);  
          
        ButtonGroup bGroup = new ButtonGroup();  
        bGroup.add(rdbtnStringbuffer);  
          
        txtStr = new JTextField();  
        txtStr.setText("str");  
        txtStr.setBounds(313, 31, 180, 33);  
        panel_1.add(txtStr);  
        txtStr.setColumns(10);  
          
        JLabel label_1 = new JLabel("输入变量名：");  
        label_1.setBounds(276, 13, 87, 15);  
        panel_1.add(label_1);  
        
        
       
        JPanel panel_2 = new JPanel();  
        panel_2.setBorder(new MatteBorder(0, 1, 1, 1, (Color) new Color(0, 0, 0)));  
        contentPane.add(panel_2, BorderLayout.CENTER);  
        panel_2.setLayout(new BorderLayout(0, 0));  
          
        splitPane = new JSplitPane();  
        splitPane.addComponentListener(new ComponentAdapter() {  
            @Override  
            public void componentResized(ComponentEvent e) {  
                divider();  
            }  
        });  
        panel_2.add(splitPane, BorderLayout.CENTER);  
          
        JScrollPane scrollPane = new JScrollPane();  
        splitPane.setLeftComponent(scrollPane);  
          
        oldSql = new JTextArea();  
        scrollPane.setViewportView(oldSql);  
         
        oldSql.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				String text = oldSql.getText();
				if(text.indexOf("append")>-1){
					rdbtnStringbuffer.setEnabled(false);
					JavaTransToSql(text);
				}else if(text.startsWith("select")||text.trim().startsWith("SELECT")){
					rdbtnStringbuffer.setEnabled(true);
					SqlTransToJava(text);  
				}else{
					 //JOptionPane.showMessageDialog(CreateSqlWin.this, "格式不正确请重新输入");  
				}
				
			}
		});
 
        JScrollPane scrollPane_1 = new JScrollPane();  
        splitPane.setRightComponent(scrollPane_1);  
          
        newSql = new JTextArea();  
        scrollPane_1.setViewportView(newSql);  
          
        JPanel panel_4 = new JPanel();  
        FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();  
        flowLayout.setAlignment(FlowLayout.LEFT);  
        panel_4.setPreferredSize(new Dimension(10, 30));  
        panel_2.add(panel_4, BorderLayout.NORTH);  
          
        JLabel lblsql = new JLabel("请在左侧输入你要格式化的SQL语句：");  
        lblsql.setHorizontalAlignment(SwingConstants.LEFT);  
        panel_4.add(lblsql);  
    }  
    
    
    /**     
    * 创建人：陈永培   
    * 创建时间：2017-5-19 上午9:22:53
    * 功能说明：sql变java    
    */
    private void SqlTransToJava(String oldSqlStr) {
		//清空  
        if(!newSql.getText().equals("")){  
            newSql.setText("");  
        }  
        String valibleName = txtStr.getText();  
        if(valibleName.trim().equals("")){  
            JOptionPane.showMessageDialog(CreateSqlWin.this, "请输入变量名！");  
            return;  
        }  
        String[] sqls = oldSqlStr.split("\n");  
        StringBuffer result = new StringBuffer();  
       
        //bufferString形式
        result.append("StringBuffer "+valibleName+" = new StringBuffer();  \n");  
        for(int i=0;i<sqls.length;i++){  
        	if(sqls[i].trim().equals(""))
        		continue;
            result.append(valibleName+".append(\" "+sqls[i].trim()+" \");\n");  
        } 
  
        newSql.setText(result.toString());
	}  
    
    /**     
    * 创建人：陈永培   
    * 创建时间：2017-5-19 上午9:23:11
    * 功能说明：  java变sql  
    */
    private void JavaTransToSql(String oldSqlStr) {
  		//清空  
          if(!newSql.getText().equals("")){  
              newSql.setText("");  
          }
          //查询变量名称
          int dot_indexOf = oldSqlStr.indexOf(".append");
          int empty_or_dot_indexOf=dot_indexOf;
          String vtr="";
          while(--empty_or_dot_indexOf>0){
        	  char charAt = oldSqlStr.charAt(empty_or_dot_indexOf);
        	  if(charAt==';'||charAt==' '){
        		  break;
        	  }
          }
          vtr=vtr+oldSqlStr.substring(empty_or_dot_indexOf, dot_indexOf);
          txtStr.setText(vtr);
          //开始去除java代码
          String[] sqls = oldSqlStr.split("\n");  
          String sql="";
          for(int i=0;i<sqls.length;i++){
        	String appendSql = sqls[i].trim();
        	if(appendSql.indexOf(".append")==-1){
        		continue;
        	}
        	
        	vtr=vtr.replace("\r", "").replace("\n", "").trim();
        	String replace=vtr+".append(\"";
        	String removeAppend = appendSql.replace(replace,"");
        	removeAppend=removeAppend.replace("\");", "");
        	removeAppend=removeAppend.trim();
          	sql =sql+ removeAppend+" ";
          } 
          String perttySql = PrettySQLFormatter.getPerttySql(sql);
          newSql.setText(perttySql.trim());
  	}  
    public void divider(){  
        splitPane.setDividerLocation(0.5);  
    }  
} 