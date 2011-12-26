/*
 * Copyright 2009 Pavel Jbanov
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.litemvc;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;

public class TemplateAction implements Action {

    private String templateName;
    private boolean evaluate;
	private Expression expression;
    
    public TemplateAction(String templateName) {
        this.templateName = templateName;
    }
    
    public TemplateAction(String templateName, boolean evaluate) {
    	this.templateName = templateName;
		this.evaluate = evaluate;
		if (evaluate) {
            try {
                expression = ExpressionFactory.createExpression(templateName);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
		}
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public boolean isEvaluate() {
		return evaluate;
	}
    
    public Expression getExpression() {
		return expression;
	}
}
