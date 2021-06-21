//
//  TabBarController.swift
//  Mousiki
//
//  Created by A.Yabahddou on 5/2/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit

class TabBarController: UITabBarController {

    var playerVC: PlayerVC!

    private static let PLAYER_HEIGHT: CGFloat = 60

    override func viewDidLoad() {
        super.viewDidLoad()
        configureTabBar()
        initPlayerView()
    }

    func maximizePlayer() {
        let frame = self.view.frame
        let height = frame.height

        // Animate
        UIView.animate(withDuration: 0.4) {
            self.playerVC.view.frame = CGRect(x: frame.minX,
                y: frame.minY ,
                width: frame.width,
                height: height)
            self.playerVC.view.layoutIfNeeded()
        }
    }

    func minimizePlayer() {
        let viewFrame = self.view.frame
        UIView.animate(withDuration: 0.4) {
            self.playerVC.view.frame = CGRect(x: viewFrame.minX,
                y: self.tabBar.frame.minY - TabBarController.PLAYER_HEIGHT,
                width: viewFrame.width,
                height: TabBarController.PLAYER_HEIGHT)
            self.view.layoutIfNeeded()
        }
    }

    private func initPlayerView() {
        let playerVC = PlayerVC.loadFromNib()
        self.playerVC = playerVC
        let frame = self.view.frame
        playerVC.view.frame = CGRect(x: 0, y: frame.maxY, width: frame.width, height: 0)
        playerVC.view.layoutSubviews()
        self.addChild(playerVC)
        self.view.addSubview(playerVC.view)
        playerVC.didMove(toParent: self)
    }

    private func configureTabBar() {

        self.tabBar.isTranslucent = false
        self.tabBar.layer.shadowOffset = CGSize.zero
        self.tabBar.layer.shadowRadius = 2
        self.tabBar.layer.shadowColor = UIColor.gray.cgColor
        self.tabBar.layer.shadowOpacity = 1

        // Customize UITabBarItem appearance
        UITabBarItem.appearance().setTitleTextAttributes([NSAttributedString.Key.font: robotoRegular(size: 14)!], for: .normal)
        UITabBarItem.appearance().setTitleTextAttributes([NSAttributedString.Key.font: robotoMedium(size: 14)!], for: .selected)
        UITabBarItem.appearance().setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.white], for: .selected)

        // Create viewControllers
        let homeVC = HomeVC.loadFromNib()
        homeVC.tabBarItem = UITabBarItem(title: "menu_home".localized, image: R.image.baseline_home_white_24pt(), tag: 0)

        let libraryVC = LibraryVC.loadFromNib()
        libraryVC.tabBarItem = UITabBarItem(title: "menu_library".localized, image: R.image.baseline_library_music_white_24pt(), tag: 1)

        let mainSearchVC = MainSearchVC.loadFromNib()
        mainSearchVC.tabBarItem = UITabBarItem(title: "menu_search".localized, image: R.image.baseline_search_white_24pt(), tag: 2)

        self.viewControllers = [homeVC, libraryVC, mainSearchVC].map {
            UINavigationController(rootViewController: $0)
        }
    }
}
