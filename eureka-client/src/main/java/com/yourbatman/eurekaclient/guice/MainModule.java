package com.yourbatman.eurekaclient.guice;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        // bind(Animal.class).to(Dog.class);
        // bind(Dog.class).toInstance(new Dog(){
        //     @Override
        //     public void run() {
        //         System.out.println("son dog");
        //         super.run();
        //     }
        // });


        // bind(new TypeLiteral<List<Animal>>(){}).toInstance(Arrays.asList(new Dog(),new Cat()));

        // Multibinder<Animal> multibinder = Multibinder.newSetBinder(binder(), Animal.class);
        // multibinder.addBinding().toInstance(new Dog());
        // multibinder.addBinding().toInstance(new Cat());


        // bind(Animal.class).annotatedWith(DogAnno.class).to(Dog.class);
        // bind(Animal.class).annotatedWith(CatAnno.class).to(Cat.class);
        // bind(Animal.class).annotatedWith(Names.named("dog")).toInstance(new Dog());
        // bind(Animal.class).annotatedWith(Names.named("cat")).toInstance(new Cat());
    }

    // @Provides
    // @Singleton
    // private Animal getAnimal(){
    //     return new Dog();
    // }
}
