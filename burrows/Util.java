/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class Util {
    public static void main(String[] args) {

    }

    public static String getStringFromBase(String base, int offset) {
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < offset + base.length(); i++) {
            char character = getIthStringsDthChar(base, i);
            String str = "" + character;
            if (character < 32 || character > 126) {
                str = "0x" + Integer.toString(character, 16);
            }
            sb.append(str + " ");
        }
        return sb.toString();
    }

    public static char getIthStringsDthChar(String base, int offset) {
        return base.charAt(offset % base.length());
    }
}
