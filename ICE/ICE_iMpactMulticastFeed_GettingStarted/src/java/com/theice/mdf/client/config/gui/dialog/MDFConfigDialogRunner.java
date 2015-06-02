package com.theice.mdf.client.config.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfigRepository;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;

/**
 * MDFConfig Dialog Runner
 * 
 * @author Adam Athimuthu
 *
 */
public class MDFConfigDialogRunner implements ActionListener
{
	JFrame mainFrame = null;
	JButton myButton = null;

	public MDFConfigDialogRunner()
	{
		mainFrame = new JFrame("MDFConfigDialog Runner");
		mainFrame.addWindowListener(
				new WindowAdapter()
					{
						public void windowClosing(WindowEvent e)
						{
							System.exit(0);
						}
					}
				);
		
		myButton = new JButton("Run MDFConfig Dialog!");
		myButton.addActionListener(this);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.getContentPane().add(myButton);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (myButton == e.getSource())
		{
			MDFClientConfigDialog dialog=new MDFClientConfigDialog(mainFrame, "MDF Client Configuration");
			
			MDFClientConfiguration mdfClientConfig=MDFClientConfigurator.getInstance().getCurrentConfiguration();
			
			if(mdfClientConfig!=null)
			{
				System.out.println("### "+mdfClientConfig.toString());
			}
			else
			{
				System.err.println("Configuration not selected.");
			}
		}
	}

	public static void main(String argv[])
	{
		try
		{
			MDFClientConfigurator config=MDFClientConfigurator.getInstance();
			config.init();
			
			MDFClientConfigRepository configRepository=config.configure();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	    
		MDFConfigDialogRunner runner=new MDFConfigDialogRunner();
	}
}

