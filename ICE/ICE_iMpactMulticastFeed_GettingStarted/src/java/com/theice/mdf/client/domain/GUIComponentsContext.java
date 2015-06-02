package com.theice.mdf.client.domain;

import org.apache.log4j.Logger;
import com.theice.mdf.client.gui.panel.PriceLevelPanel;
import com.theice.mdf.client.gui.panel.BookPanel; 

public class GUIComponentsContext
{
   private static Logger logger = Logger.getLogger(GUIComponentsContext.class);
   
   //guarded by the owning Market object
   private PriceLevelPanel _priceLevelPanel = null;

   //guarded by the owning Market object   
   private BookPanel _bookPanel = null;
   
   public GUIComponentsContext()
   {
   }
   
   public void setPriceLevelPanel(PriceLevelPanel panel)
   {
      this._priceLevelPanel = panel;
   }
   
   public void setBookPanel(BookPanel panel)
   {
      this._bookPanel = panel;
   }
   
   public void displayPanelInitializingText(String str)
   {
      logger.info("displayPanelInitializingText: str="+str);
      if (_priceLevelPanel != null)
      {
         _priceLevelPanel.appendDenomLabelText(str);
      }
      if (_bookPanel != null)
      {
         _bookPanel.appendDenomLabelText(str);
      }
   }
   
   public void clearPanelInitializingText()
   {
      logger.info("clearPanelInitializingText");
      if (_priceLevelPanel != null)
      {
         _priceLevelPanel.resetDenomLabelText();
      }
      if (_bookPanel != null)
      {
         _bookPanel.resetDenomLabelText();
      }      
   }
}
