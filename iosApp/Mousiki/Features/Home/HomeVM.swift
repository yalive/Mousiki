//
//  HomeVM.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/1/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared

class HomeVM {
    
    fileprivate weak var delegate: HomeVMDelegate?
    fileprivate lazy var homeRepository = Injector().homeRepository
    fileprivate lazy var appConfig = Injector().appConfig
    fileprivate lazy var getCountryArtists = Injector().getCountryArtists
    fileprivate lazy var getGenres = Injector().getGenres
    fileprivate lazy var getTrending = Injector().getNewReleasedSongs
    
    init(delegate: HomeVMDelegate) {
        self.delegate = delegate
        print("Home view model is waiting for activation of remote config")
        appConfig.awaitActivation { (kUnit, error) in
            print("Home view model finished waiting")
            self.loadHome()
        }
    }
    
    func loadHome() {
        if !appConfig.doNewHomeEnabled() {
            homeRepository.getHome { (result, error) in
                if result is ResultSuccess<HomeRS> {
                    let homeRS = (result as! ResultSuccess<HomeRS>).data
                    self.delegate?.showHome(homeRS!)
                    self.loadTrending()
                    self.loadArtists()
                } else {
                    self.showOldHome()
                }
            }
        } else {
            self.showOldHome()
        }
    }
    
    func loadTrending(){
        getTrending.invoke(max: 10, lastKnown: nil) { (result, error) in
            if result is ResultSuccess<NSArray> {
                let tracksArray = ((result as! ResultSuccess<NSArray>).data) as! [MusicTrack]
                let items = tracksArray.map { $0.toDisplayedVideoItem()}
                self.delegate?.showTrending(items)
            }
        }
    }
    
    func loadArtists() {
        getCountryArtists.invoke { (result, error) in
            if result is ResultSuccess<NSArray> {
                let artists = ((result as! ResultSuccess<NSArray>).data) as! [Artist]
                self.delegate?.showArtists(artists)
                print("Got \(artists.count) artists")
            }
        }
    }
    
    fileprivate func showOldHome() {
        self.loadTrending()
        DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
            self.loadArtists()
        }
        
    }
}

protocol HomeVMDelegate: class {
    
    func showHome(_ homeRS: HomeRS)
    
    func showTrending(_ items: [DisplayedVideoItem])
    
    func showArtists(_ artists: [Artist])
    
}
