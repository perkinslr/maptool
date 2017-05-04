/*
 * This software Copyright by the RPTools.net development team, and licensed
 * under the GPL Version 3 or, at your option, any later version.
 *
 * MapTool 2 Source Code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this source Code. If not, see <http://www.gnu.org/licenses/>
 */
package net.rptools.maptool.client.script.javascript.api;

import net.rptools.maptool.model.Token;

import java.util.Iterator;
import java.util.Set;

public class JSAPIToken {
	private final Token token;
	private Set<String> names;
	private Iterator<String> names_iter;

	public JSAPIToken(Token token) {
		this.token = token;
	}

	public String getName() {
		return token.getName();
	}

	public void setName(String name) {
		token.setName(name);
	}

	public boolean hasSight() {
		return token.getHasSight();
	}

	public void setSight(boolean sight) {
		token.setHasSight(sight);
	}

	public String toString() {
		return "Token(id=" + token.getId() + ")";
	}

	public void iteratePropertyNames() {
		names = this.token.getPropertyNames();
		names_iter = names.iterator();
	}


	public String getNextName() {
		if (names_iter.hasNext()) {
			return names_iter.next();
		}
		return "";

	}

	public String getId() {
		return "" + token.getId();
	}

	public String getProperty(String name) {
		return "" + this.token.getProperty(name);
	}

}
