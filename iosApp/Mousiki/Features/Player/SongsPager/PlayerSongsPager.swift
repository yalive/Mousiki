//
//  PlayerSongsPager.swift
//  Mousiki
//
//  Created by A.Yabahddou on 5/3/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

protocol PlayerSongsPagerDelegate: AnyObject {
    func didScrollToSong(_ song: DisplayedVideoItem)
}

class PlayerSongsPager: UICollectionView {

    var songs: [DisplayedVideoItem] = [] {
        didSet {
            self.reloadData()
        }
    }


    weak var pagerDelegate: PlayerSongsPagerDelegate?
    private var currentIndex = -1

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.dataSource = self
        self.delegate = self
        self.register(UINib.init(nibName: "PlayerSongCell", bundle: nil), forCellWithReuseIdentifier: "pagerCell")
        self.isPagingEnabled = true
        self.showsVerticalScrollIndicator = false
        self.showsHorizontalScrollIndicator = false
        self.backgroundColor = .clear
        let flowLayout = UICollectionViewFlowLayout()
        flowLayout.scrollDirection = .horizontal
        self.collectionViewLayout = flowLayout
    }

}

extension PlayerSongsPager: UICollectionViewDataSource {

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        songs.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "pagerCell", for: indexPath as IndexPath) as! PlayerSongCell
        cell.song = songs[indexPath.row]
        return cell
    }

}

extension PlayerSongsPager: UICollectionViewDelegateFlowLayout {

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: self.frame.width, height: self.frame.width)
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }

    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        let previousIndex = self.currentIndex
        self.currentIndex = calculateCurrentPage(scrollView)
        print("Scroll to \(currentIndex)")
        if previousIndex != self.currentIndex {
            let song = songs[self.currentIndex]
            self.pagerDelegate?.didScrollToSong(song)
        }

    }

    func scrollViewDidEndScrollingAnimation(_ scrollView: UIScrollView) {
        self.currentIndex = calculateCurrentPage(scrollView)
        print("Scroll to \(currentIndex) with animation")
    }

    private func calculateCurrentPage(_ scrollView: UIScrollView) -> Int {
        if songs.isEmpty { return 0 }
        return Int(scrollView.contentOffset.x) / Int(scrollView.frame.width) % songs.count
    }
}
