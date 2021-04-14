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
package net.rptools.maptool.client.macro.impl;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolMacroContext;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.script.javascript.*;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.TextMessage;

@MacroDefinition(
    name = "javascript",
    aliases = {"js"},
    description = "javascript.description")
public class JavascriptMacro extends AbstractMacro {
  public void execute(MacroContext context, String macro, MapToolMacroContext executionContext) {
    if (MapTool.getFrame()
        .getCommandPanel()
        .fromMacroButton) { // /js does nothing if not called directly from the command panel
      return;
    }
    macro = macro.replace("\\{", "{").replace("\\}", "}");
    String result =
        "" + JSScriptEngine.getJSScriptEngine().evalMacro(macro, MapTool.getPlayer().isGM(), null);
    if (result != null) {
      MapTool.addMessage(
          new TextMessage(
              TextMessage.Channel.ALL,
              null,
              MapTool.getPlayer().getName(),
              "* " + I18N.getText("roll.string", MapTool.getPlayer().getName(), result),
              context.getTransformationHistory()));
    }
  }
}
