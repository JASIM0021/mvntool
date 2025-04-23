//package org.mvntool.utils;
//
//
//@Command(name = "update", description = "Update the tool and dependency database")
//public void update(
//        @Option(names = {"-f", "--force"}, description = "Force update") boolean force) {
//
//    Updater updater = new Updater();
//    if (updater.checkForUpdates() || force) {
//        updater.downloadLatest();
//        System.out.println("Update complete!");
//    } else {
//        System.out.println("Already up to date");
//    }
//}