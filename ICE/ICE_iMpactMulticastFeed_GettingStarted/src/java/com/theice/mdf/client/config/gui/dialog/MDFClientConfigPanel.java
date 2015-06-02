package com.theice.mdf.client.config.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.domain.book.MulticastChannelContext;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MDFClientConfigPanel extends JPanel
{
	protected JComboBox environmentsComboBox=null;
	private static final String defaultEnvironment="apitest";
	
    public static final String actionFullOrderDepth=MulticastChannelContext.FULLORDERDEPTH.toString();
    public static final String actionPriceLevel=MulticastChannelContext.PRICELEVEL.toString();
	
	protected JRadioButton radioFullOrderDepth=null;
	protected JRadioButton radioPriceLevel=null;
	
	public MDFClientConfigPanel(ActionListener listener)
	{
		super(new BorderLayout());
    	setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"MDF Client Config",
        		TitledBorder.LEADING,TitledBorder.TOP,MDFUtil.fontArialBold12));
        setFont(MDFUtil.fontArialPlain12);
        
        add(createInformationTextPane(),BorderLayout.NORTH);

        JPanel choicePanel=new JPanel(new FlowLayout());
    	String[] environments=MDFClientConfigurator.getInstance().getConfigRepository().getEnvironments();
    	
    	/**
    	 * Init the combo box with the default of "apitest" (if available)
    	 * Do this before the action listener is added, in order to avoid exceptions
    	 */
    	environmentsComboBox=new JComboBox(environments);
		environmentsComboBox.setSelectedItem(defaultEnvironment);
        
        environmentsComboBox.addActionListener(listener);
        choicePanel.add(new JLabel("Environment : "));
        choicePanel.add(environmentsComboBox);
        
        ButtonGroup buttonGroup=new ButtonGroup();

        radioFullOrderDepth=new JRadioButton(actionFullOrderDepth);
        radioFullOrderDepth.addActionListener(listener);
        radioFullOrderDepth.setSelected(true);
        buttonGroup.add(radioFullOrderDepth);
        
        radioPriceLevel=new JRadioButton(actionPriceLevel);
        radioPriceLevel.addActionListener(listener);
        buttonGroup.add(radioPriceLevel);
        
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(radioFullOrderDepth);
        radioPanel.add(radioPriceLevel);
        choicePanel.add(new JLabel("Multicast Context : "));
        choicePanel.add(radioPanel);
        
        add(choicePanel,BorderLayout.CENTER);
	}
	
	/**
	 * Create information text pane
	 * @return
	 */
	public JComponent createInformationTextPane()
	{
        String text="<html><body><table><tr><td>Select the environment/multicast context. "+
		"Then choose the Multicast Group and press the Ok button. "+
		"<font color=red><b>For test environments, please make sure the corresponding multicast tunnel proxy is running."+
		"</b></font></td></tr></table><body></html>";

        JTextPane textPane=new JTextPane();
        textPane.setContentType("text/html");
		textPane.setEditable(false);
		textPane.setBackground(Color.white);
		textPane.setBorder(BorderFactory.createLineBorder(Color.black));
		textPane.setText(text);
		
        return(textPane);
	}

	public JComboBox getEnvironmentsComboBox()
	{
		return(this.environmentsComboBox);
	}
	
	public JRadioButton getFullOrderDepthRadioButton()
	{
		return(this.radioFullOrderDepth);
	}
	
	public JRadioButton getPriceLevelRadioButton()
	{
		return(this.radioPriceLevel);
	}
	
	private boolean isFullOrderDepthSelected()
	{
		return(radioFullOrderDepth.isSelected());
	}
	
	private boolean isPriceLevelSelected()
	{
		return(radioPriceLevel.isSelected());
	}
	
	public String getSelectedEnvironment()
	{
		return((String) this.environmentsComboBox.getSelectedItem());
	}
	
	public MulticastChannelContext getSelectedMulticastContext()
	{
		MulticastChannelContext context=MulticastChannelContext.FULLORDERDEPTH;

		if(this.isFullOrderDepthSelected())
		{
			context=MulticastChannelContext.FULLORDERDEPTH;
		}
		else if(this.isPriceLevelSelected())
		{
			context=MulticastChannelContext.PRICELEVEL;
		}
		
		return(context);
	}
	
	/**
	 * Create the information text
	 * @return
	 * @deprecated
	 */
	public JComponent createInformationTextEditorPane()
	{
        String text="Select the environment/multicast context. "+
		"Then choose the Multicast Group and press the <Ok> button. "+
		"For test environments, please make sure the corresponding multicast tunnel proxy is running.";

        DefaultStyledDocument document=new DefaultStyledDocument();

        JEditorPane editorPane=new JEditorPane();
        editorPane.setDocument(document);
		editorPane.setEditable(false);
		editorPane.setBackground(Color.lightGray);
		editorPane.setForeground(Color.black);
		editorPane.setBorder(BorderFactory.createLineBorder(Color.black));
		editorPane.setText(text);
		
        MutableAttributeSet attributes=new SimpleAttributeSet();
        StyleConstants.setBold(attributes,true);
        StyleConstants.setBackground(attributes, Color.red);
        
        editorPane.setSelectionStart(0);
        editorPane.setSelectionEnd(10);
        
        document.setCharacterAttributes(0, 10, attributes, true);

        return(editorPane);
	}

	/**
	 * @deprecated
	 * @return
	 */
	public JComponent createInformationTextPlain()
	{
        String text="Select the environment/multicast context. "+
		"Then choose the Multicast Group and press the <Ok> button. "+
		"For test environments, please make sure the corresponding multicast tunnel proxy is running.";

		JTextArea textArea=new JTextArea(3,50);
		textArea.setEditable(false);
		textArea.setBackground(Color.lightGray);
		textArea.setForeground(Color.black);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBorder(BorderFactory.createLineBorder(Color.black));
		textArea.setText(text);
		
		return(textArea);
	}

}

