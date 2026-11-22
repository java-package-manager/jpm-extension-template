package dev.example.hello;

import dev.jpm.api.client.Results;
import dev.jpm.api.json.Jsonc;
import dev.jpm.api.model.Project;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {
    @SuppressWarnings("unchecked")
    private static Project project() {
        String load = """
                { "dir": "/p", "name": "shop", "version": "1.0.0", "workspace": true,
                  "modules": [{ "name": "core", "version": "1.0.0", "dir": "/p/core" }, { "name": "app", "version": "1.2.0", "dir": "/p/app" }] }
                """;
        return Results.project((Map<String, Object>) Jsonc.parse(load));
    }

    @Test
    void greetsEveryModule() {
        assertEquals("jpm says hello to core 1.0.0\njpm says hello to app 1.2.0\n", Main.greetings(project(), new Main.Options("jpm", false)));
        assertEquals(List.of(Map.of("module", "core", "greeting", "ann says hello to core 1.0.0"), Map.of("module", "app", "greeting",
                "ann says hello to app 1.2.0")), Jsonc.parse(Main.greetings(project(), new Main.Options("ann", true))));
    }

    @Test
    void parsesItsOptions() {
        assertEquals(new Main.Options("ann", true), Main.parse(List.of("--name", "ann", "--json")));
        assertNull(Main.parse(List.of("--help")));
        assertThrows(IllegalArgumentException.class, () -> Main.parse(List.of("--name")));
        assertThrows(IllegalArgumentException.class, () -> Main.parse(List.of("--loud")));
    }
}
