# Scenes

Restricts entity visibility to players inside WorldGuard regions.

Large amounts of entities reduce client-side performance.
This happens even if the entities are not visible, for example, if they are behind walls.

## Installation

This plugin requires **Paper 1.21.11**.

Download and place the `Scenes.jar` file in your `plugins` folder.

## Controlling visibility

Scenes allows grouping entities using tags.
Entities without any tags are always visible.

```
/scenes entity @e[type=armor_stand,sort=nearest,limit=1] tag add hello_world
```

Then, tags can be shown or hidden. This shows or hides all entities assigned to the tag.

```
/scenes show hello_world
```

Instead of using a command, you can control tag visibility using the following integrations.

### WorldGuard regions

If WorldGuard is installed, tags can be added to WorldGuard regions using the `scenes` flag.
This flag contains a comma-separated list of tags.

Players will see any entities assigned to any tag of any region they are standing in.

```
/rg flag my_region scenes hello_world,another_tag
```

### TrainCarts trains

If TrainCarts is installed, tags can be added to trains using a TrainCarts sign.

Passengers of a train will see any entities assigned to any tag which the train also has.

```
[+train]
scene show
hello_world
another_tag
```

Each line can contain a single tag.
More than two scene names can be specified by placing another sign below (just like the `announce` sign).

| Sign          | Description                                                                                        |
|---------------|----------------------------------------------------------------------------------------------------|
| `scene show`  | Adds the specified scene tags to the train. Passengers will start seeing entities with those tags. |
| `scene hide`  | Removes the specified scene tags from the train.                                                   |
| `scene set`   | Replaces the list of scene tags of the train with the specified tags.                              |
| `scene clear` | Removes all scene tags from the train.                                                             |

## Permissions

| Permission                | Description                                                                 |
|---------------------------|-----------------------------------------------------------------------------|
| `scenes.assign.entity`    | Allows assigning entities to scene tags using `/scenes entity ... tag ...`. |
| `scenes.manual`           | Allows using `/scenes show` and `/scenes hide` to manually show scene tags. |
| `scenes.traincarts.build` | Allows placing Scenes TrainCarts signs.                                     |

## API

Scenes offers an API which allows other plugins to register their objects into scenes.

### Gradle

```kotlin
repositories {
    maven("https://repo.bundlegroup.gg/repository/maven-public/")
}

dependencies {
    compileOnly("gg.bundlegroup.scenes:scenes-api:1.1.0")
}
```

### Usage

#### Controllers

Controllers allow showing scene tags to players.
For example, the built-in WorldGuard integration uses a controller to show scenes to players in certain regions.

```java
Controller controller = Scenes.get().createController(plugin);
controller.showTag(player, "hello_world");
```

#### Elements

Elements are objects which can be shown or hidden.
For example, all entities are automatically registered as elements.

```java
Element element = Scenes.get().createElement(plugin, viewable);
element.addTag("hello_world");
```

The passed `Viewable` object needs to implement these methods:

```java
@Override
public void addViewer(Player player) {
}

@Override
public void removeViewer(Player player) {
}
```

Call `element.remove();` to unregister your element when it no longer exists.
