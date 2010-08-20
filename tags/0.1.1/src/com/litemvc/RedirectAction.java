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

public class RedirectAction implements Action {

    private String location;
    private boolean evaluate;
    private Expression expression;
    
    public RedirectAction(String location) {
        this(location, false);
    }
    
    public RedirectAction(String location, boolean evaluate) {
        this.location = location;
        this.evaluate = evaluate;
        
        if (evaluate) {
            try {
                expression = ExpressionFactory.createExpression( location );

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
    
    public boolean isEvaluate() {
        return evaluate;
    }
    
    public Expression getExpression() {
        return expression;
    }
    
    public String getLocation() {
        return location;
    }
}
