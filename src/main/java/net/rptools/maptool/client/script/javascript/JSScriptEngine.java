/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * MapTool Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.maptool.client.script.javascript;

import java.util.*;
import javax.script.*;
import net.rptools.maptool.client.script.javascript.api.MapToolJSAPIDefinition;
import net.rptools.maptool.client.script.javascript.api.MapToolJSAPIInterface;
import net.rptools.maptool.model.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

public class JSScriptEngine {

  private static JSScriptEngine jsScriptEngine = new JSScriptEngine();
  private static final Logger log = LogManager.getLogger(JSScriptEngine.class);

  private ScriptEngine engine;
  private Map<String, ScriptContext> untrustedContext;
  private Map<String, ScriptContext> trustedContext;

  private void registerAPIObject(ScriptContext context, MapToolJSAPIInterface apiObj) {
    MapToolJSAPIDefinition def = apiObj.getClass().getAnnotation(MapToolJSAPIDefinition.class);
    Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
    bindings.put(def.javaScriptVariableName(), apiObj);
  }

  private JSScriptEngine() {
    engine = new ScriptEngineManager().getEngineByName("graal.js");

    trustedContext = new HashMap<String, ScriptContext>();
    untrustedContext = new HashMap<String, ScriptContext>();
  }

  private ScriptContext getContext() {
    ScriptContext newContext = new SimpleScriptContext();
    newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
    Reflections reflections = new Reflections("net.rptools.maptool.client.script.javascript.api");
    Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(MapToolJSAPIDefinition.class);

    for (Class<?> apiClass : annotated) {
      try {
        if (MapToolJSAPIInterface.class.isAssignableFrom(apiClass)) {
          registerAPIObject(newContext, (MapToolJSAPIInterface) apiClass.newInstance());
        } else {
          log.error("Could not add API object " + apiClass.getName() + " (missing interface)");
        }
      } catch (Exception e) {
        log.error("Could not add API object " + apiClass.getName(), e);
      }
    }
    return newContext;
  }

  public static JSScriptEngine getJSScriptEngine() {
    return jsScriptEngine;
  }

  public Object evalMacro(String macroBody, boolean trusted, Token token) {
    return evalMacro(macroBody, trusted, token, "/repl");
  }

  public Object evalMacro(String macroBody, boolean trusted, Token token, String jsContext) {
    Map<String, ScriptContext> ctxMap;
    if (trusted) {
      ctxMap = trustedContext;
    } else {
      ctxMap = untrustedContext;
    }

    ScriptContext ctx;
    if (jsContext == null) {
      ctx = getContext();
    } else {

      if (!ctxMap.containsKey(jsContext)) {
        ctxMap.put(jsContext, getContext());
      }
      ctx = ctxMap.get(jsContext);
    }

    try {
      return engine.eval(macroBody.toString(), ctx);
    } catch (ScriptException e) {
      return "" + e;
    }
  }

  public Object evalAnonymous(String script) throws ScriptException {

    StringBuilder wrapped = new StringBuilder();
    wrapped
        .append("(function() { var args = MTScript.getMTScriptCallingArgs(); ")
        .append(script)
        .append("})();");
    return engine.eval(wrapped.toString(), getContext());
  }
}
