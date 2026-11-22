# jpm extension template

A minimal [jpm](https://github.com/java-package-manager/jpm) extension, `jpm hello`, to start a new one from.
It greets every module of the project it runs in, which shows the parts every extension has:

- **The manifest** (`project.jsonc`): an extension is a jpm project whose name is `jpm-<command>`, with a
  `main` class and an `"extension"` descriptor. The descriptor lists its commands, their options and the
  checks it adds to `jpm check`; jpm hands it to IDEs (the `extensions/list` request of the jpm protocol)
  without running the extension, and `jpm add` uses it to recognise an extension.
- **Talking to jpm** (`src/dev/example/hello/Main.java`): jpm runs the extension with `JPM_EXE` (the jpm to
  call back) and `JPM_PROJECT_DIR`, `JPM_MODULE_DIR`, `JPM_MANIFEST`, `JPM_CONFIG` set. The extension asks
  that jpm about the project with jpm's client, `dev.jpm:jpm-api` (`JpmClient`, `jpm api`), and never reads
  `project.jsonc` itself.
- **Settings**: users configure an extension in their `project.jsonc` under `"extensions": { "hello": { ... } }`;
  `JPM_CONFIG` carries the module's manifest as JSON.

## Make it yours

1. Create a repository from this template, named `jpm-<command>`.
2. In `project.jsonc`, change `name`, `group`, `description`, `main` and the `"extension"` commands; move
   `src/dev/example/hello` (and the test) to your package.
3. Keep the descriptor in step with what `main` accepts: IDEs show exactly what it lists.

## Use it

```sh
jpm add github:<you>/jpm-<command>      # declare it in a project (pinned in project.lock)
jpm add -g github:<you>/jpm-<command>   # or install it for every project
jpm <command>
```

jpm installs Git extensions from their tags: tag a release with the version in `project.jsonc` (`0.1.0`), and
`jpm add` picks the newest tag.

## Develop

```sh
jpm test
jpm build
JPM_EXE=jpm JPM_PROJECT_DIR=/path/to/a/project jpm start -- --name you
```
