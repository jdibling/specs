package com.theice.mdf.client;

import com.theice.logging.core.domain.ManagedObject;
import com.theice.logging.domain.ComponentStatus;

public class ClientState implements ManagedObject
{
   private String _key="MCSnapshotClient";
   private String _type="MCFeedClient";
   private String _name="MCSnapshotClient";
   private String _deploymentInfo;
   private ComponentStatus _status=ComponentStatus.UNKNOWN;
   
   public ClientState(String key, String type, String name, String deploymentInfo)
   {
      this._key = key;
      this._type = type;
      this._name = name;
      this._deploymentInfo = deploymentInfo;
   }
   
   public void setComponentStatus(ComponentStatus status)
   {
      this._status=status;
   }
   
   @Override
   public ComponentStatus getComponentStatus()
   {
      return _status;
   }

   @Override
   public String getManagedObjectDeploymentInfo()
   {
      return _deploymentInfo;
   }

   @Override
   public String getManagedObjectKey()
   {
      return _key;
   }

   @Override
   public String getManagedObjectName()
   {
      return _name;
   }

   @Override
   public String getManagedObjectType()
   {
      return _type;
   }

   @Override
   public boolean qualifyManagedObjectWithEnvironment()
   {
      return false;
   }
   
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_key == null) ? 0 : _key.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof ClientState))
         return false;
      final ClientState other = (ClientState) obj;
      if (_key == null)
      {
         if (other._key != null)
            return false;
      }
      else if (!_key.equals(other._key))
         return false;
      return true;
   }

}
