package gg.bundlegroup.bundlescenes.traincarts.action;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.properties.api.IStringSetProperty;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface SceneAction {
    static SceneAction parse(SignActionEvent info) {
        String action = parseAction(info);
        return switch (action) {
            case "show" -> new ShowSceneAction(parseScenes(info));
            case "hide" -> new HideSceneAction(parseScenes(info));
            case "set" -> new SetSceneAction(parseScenes(info));
            case "clear" -> new ClearSceneAction();
            default -> new InvalidSceneAction();
        };
    }

    private static String parseAction(SignActionEvent info) {
        String line = info.getLine(1);
        int index = line.indexOf(' ');
        if (index == -1) {
            return "";
        }
        return line.substring(index + 1).trim();
    }

    private static Set<String> parseScenes(SignActionEvent info) {
        Set<String> scenes = new HashSet<>();
        scenes.add(info.getLine(2));
        scenes.add(info.getLine(3));
        scenes.addAll(Arrays.asList(info.getExtraLinesBelow()));
        scenes.remove("");
        return scenes;
    }

    void execute(MinecartMember<?> member, IStringSetProperty property);

    @Nullable String getDescription();
}
