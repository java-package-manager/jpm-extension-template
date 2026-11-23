package dev.example.hello;

import dev.jpm.api.client.JpmClient;
import dev.jpm.api.client.JpmError;
import dev.jpm.api.client.LoadOptions;
import dev.jpm.api.json.Jsonc;
import dev.jpm.api.model.Module;
import dev.jpm.api.model.Project;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@code jpm hello}: jpm runs this as {@code jpm-hello} with {@code JPM_EXE} (the jpm to talk back to) and
 * {@code JPM_PROJECT_DIR} set, and the extension asks that jpm about the project over its protocol.
 */
public final class Main {
    private Main() {
    }

    /**
     * The options of {@code jpm hello}, as the descriptor in project.jsonc lists them.
     */
    record Options(String name, boolean json) {}

    public static void main(String[] args) {
        Options o;
        try {
            o = parse(List.of(args));
        } catch (IllegalArgumentException e) {
            System.err.println("error: " + e.getMessage());
            System.exit(2);
            return;
        }
        if (o == null) {
            System.out.println("Usage: jpm hello [--name <name>] [--json]");
            return;
        }
        String jpm = env("JPM_EXE", "jpm");
        Path dir = Path.of(env("JPM_PROJECT_DIR", "."));
        try (JpmClient client = JpmClient.start(List.of(jpm, "api"), dir, Map.of(), "jpm-hello", "0.1.0")) {
            System.out.print(greetings(client.load(dir, LoadOptions.defaults()), o));
        } catch (JpmError e) {
            System.err.println("error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * The options, or null for {@code --help}.
     */
    static Options parse(List<String> args) {
        String name = "jpm";
        boolean json = false;
        for (int i = 0; i < args.size(); i++) {
            switch (args.get(i)) {
                case "--help", "-h" -> {
                    return null;
                }
                case "--json" -> json = true;
                case "--name" -> {
                    if (i + 1 >= args.size()) throw new IllegalArgumentException("--name needs a name");
                    name = args.get(++i);
                }
                default -> throw new IllegalArgumentException("unknown option " + args.get(i) + "; see jpm hello --help");
            }
        }
        return new Options(name, json);
    }

    /**
     * One greeting per module, as text or as JSON.
     */
    static String greetings(Project p, Options o) {
        List<Object> out = new ArrayList<>();
        StringBuilder text = new StringBuilder();
        for (Module m : p.modules()) {
            String line = o.name() + " says hello to " + m.name() + " " + m.version();
            out.add(Map.of("module", m.name(), "greeting", line));
            text.append(line).append('\n');
        }
        return o.json() ? Jsonc.write(out) + "\n" : text.toString();
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return v == null || v.isBlank() ? fallback : v;
    }
}
