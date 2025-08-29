# KTX

A reactivity-ready DSL for Kotlin to build web-frameworks on

This module provides a DSL to create HTML5 elements in Kotlin with support for reactivity. This library itself does very
little on its own, but is meant to be used as a building block for web-frameworks.

BYORM (Bring your own reactivity model) - This library does not provide any reactivity model on its own!

## Example of KTX DSL

```kotlin
script(src = { "https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4" }) {}
div(`class` = { "flex flex-col gap-4" }) {
    h1(`class` = { "text-3xl font-bold" }) {
        +"TODO List"
    }
    h3(`class` = { "text-xl color-gray-600 m-1" }, onClick = { println("Clicked h3") }) {
        +{ taskList.size() + " Tasks" }
    }
}
```

## How to embed KTX in your project

#### Provide a user entry point

You need to provide the user with some method that they can mount their KTX into.
This method needs to provide a parameter that allows the user to provide a body.
If you want the user to write the whole html you would use `HtmlBody` as type, if you provide a pre-made div to mount
into like
`<div id="app"></div>` you would use `DivBody` as type.

```kotlin
fun startYourFramework(body: DivBody) {
    ...
}
```

#### Render/Use the body

The body is a not-yet computed tree of the user provided KTX so its not a full tree and up to you to render it.
All `body`s (`DivBody`, `HtmlBody`, ...) are function types, that means at the point `startYourFramework` is called the
user code
has not been executed yet - that means you can decide when and how to execute it to fit it into your reactive model.

To render a body you can just call the `render` function, all native html bodies provide a render function
> You might need to import `render` from `eu.niton.ktx.tags.content` or `eu.niton.ktx.tags`

```kotlin
fun startYourFramework(body: DivBody) {
    val ktxElement = render(body)
}
```

#### Use the ktxElement

Now you have a KTX element - this is not a full tree yet, its just a node of type [
`KtxElement`](src/runtime/kotlin/eu/niton/ktx/KtxElement.kt).
There are four types of KTX elements:

- `String`: A text node added via `+"some text"` (`+{"some text"}` is dynamic ->
  `KtxElement.Function { KtxElement.String("some text") }`)
- `Function`: A "to be evaluated" ktx element, this allows for dynamic content that changes
- `Tag`: A html tag with attributes, listeners and a body function that can be rendered to get the children (again the
  tag has no children its up to you to render them)
- `List`: A list of KTX elements, this is used when you add multiple children to a tag

based on the type you can decide what you want to do to the dom/your rendering target.

#### Creating dynamic Tags/Flow Tags

In most scenarios you will want to have tags for dynamic content like

```kotlin
div {
    If(someCondition) {
        p { +"Condition is true" }
    }.Else {
        p { +"Condition is false" }
    }
    p { +"The winners are" }
    ul {
        For(winners) {
            li { +it.name }
        }
    }
}
```

Here is an example implementation of `If` so you can get an idea of how to implement such tags:

```kotlin
inline fun <T : Content<I>, I : RenderableContent> T.If(
    crossinline condition: () -> Boolean,
    crossinline body: I.() -> Unit
): ElseFn<I> {
    var `else`: (I.() -> Unit)? = null
    +KtxElement.Function {
        if (condition()) render(this, body)
        else `else`?.let { render(this, it) }
    }
    return ElseFn { `else` = it }
}
```

What is going on here?

1. `<T : Content<I>, I : RenderableContent>`: There is no decision to be made here this is just the "This is a generic
   component" declaration. Generic work - sadly required. While not pretty it is the best solution for end-user
   experience.
2. `inline` for performance reasons tags/components which take a body (`I.() -> Unit`) should always be inline - all our
   functions are as well
3. `crossinline`: Bodies should always be `crossinline`. For other parameters you can decide yourself (like `condition`
   here)
4. `var else ...`: This is just so you can chain call the .Else function - you can freely choose return types
5. `+KtxElement.Function`: `Content` has an operator that allows to add arbitrary KtxElements to it, in this case we
   add a `Function` element that will be evaluated when the tree is rendered.
6. `render(this, body)`: We convert the `body` into an KtxElement that is the return value of the KtxElement.Function.
   Keep in mind since it is wrapped by a KtxElement.Function it will not be executed immediately and everytime when you
   have dynamic content that is able to change later you should wrap in a KtxElement.Function.
7. `return ElseFn { else = it }`: You do not need to return anything Unit is fine - but you can to allow chaining calls like
   `If { ... }.Else { ... }` - `ElseFn` is just a small wrapper class that holds the else body.

## Modules
| group        | artifact  | description                                                                                                                  |
|--------------|-----------|------------------------------------------------------------------------------------------------------------------------------|
| eu.niton.ktx | codegen   | A kotlin code generator that can be used to create KTX DSLs (HTML, SVG, arbitrary XML format)                                |
| eu.niton.ktx | html5     | A html5 KTX dsl, this is what most apps targeting html/web should use                                                        |
| eu.niton.ktx | processor | A Kotlin Symbol Processor that integrates `codegen` into kotlin compile process - not for general use yet                    |
| eu.niton.ktx | runtime   | The base of KTX, not a dsl itself but the components to build one. If you want to build a KTX DSL without codegen - use this |  