package com.brainsoon.jbpm.utils;
import java.util.List;
import org.jbpm.api.model.Transition;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.model.TransitionImpl;

public class FreeTransUtil {
	  /**
	   * 创建一条由sourceName到destName的自由线
	   * @param pd
	   * @param sourceName
	   * @param destName
	   */
	  public static void  removeOutTransition(ProcessDefinitionImpl pd,String sourceName,String destName){
		  ActivityImpl sourceActivity = pd.findActivity(sourceName);
		  List<Transition> trans=(List<Transition>) sourceActivity.getOutgoingTransitions();
		  for(Transition tran:trans){ 
			  if(tran.getDestination()!=null&&destName.equals(tran.getDestination().getName())){	   
				  trans.remove(tran);
				  break;
			  }
		  } 
		}
	    /**
	     * 删除一条由sourceName到destName的自由线
	     * @param pd
	     * @param sourceName
	     * @param destName
	     */
		public static void addOutTransition(ProcessDefinitionImpl pd,String sourceName,String destName){
		    ActivityImpl sourceActivity = pd.findActivity(sourceName);
		    ActivityImpl destActivity=pd.findActivity(destName);
		    TransitionImpl transition = sourceActivity.createOutgoingTransition();
		    transition.setName(destName);
		    transition.setDestination(destActivity);
		    sourceActivity.addOutgoingTransition(transition);
		}
}
