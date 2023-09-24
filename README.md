# asmrdroid
An android app for remixing input asmr audio into new randomized audio.

Building for compatibility with Android Oreo (8.0). This is alpha code, and lots of functionality is not yet implemented. The home page gives the user an overview of the database state, and the ability to start a fresh database if needed. The import page gives the user the ability to import a file into the app to use for generating new audio. So far the app will work if a single wav format audio file is imported (if multiple files are imported the samples will require converting to be compatible with each other, this is not implemented yet). The app will copy the file to its internal folder before generating short samples (lengths currently set by constant variables in the code but I intend to implement user selectable sample lengths). The reason the audio is copied in first is so in the future I can decide to generate samples on the fly from the imported audio, rather than storing the samples long term in the database. The app is visually designed to allow for tagging imported audio, but this functionality is not implemented yet.

Once the samples have been generated, going to the Generate tab will allow the user to select how much audio they want to generate (and eventually what audio tags they'd like the audio to be constructed from). The app will then start constructing the new audio file. This can take a long time, but since the construction begins from the start of the file, and in my tests construction occurs at more than 1 second of audio per second, it is possible to begin listening to the audio straight away in your favourite listening app while the construction continues.

A single third party library was used to manage Wav file reading/writing/header management.

I am currently retaining all other copyright as this app is for a course project, but the source code is freely available here and I'm happy for anyone to download/build/use the software for now.
