//
//  HomeGenreCell.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/22/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

protocol HomeGenreCellDelegate {
    func didTapGenre(_ genre: GenreMusic)
}

class HomeGenreCell: UITableViewCell {
    
    var delegate: HomeGenreCellDelegate?
    
    @IBOutlet var views: [UIView]!
    @IBOutlet var labels: [UILabel]!
    @IBOutlet var images: [UIImageView]!
    
    fileprivate var genres = [GenreMusic]()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        for (index, view) in views.enumerated() {
            view.tag = index
            view.setCorner(radius: 4)
            images[index].setCorner(radius: 4)
            let angle: CGFloat = 20.0 * CGFloat.pi / 180.0
            
            var transformation = CGAffineTransform(rotationAngle: CGFloat(angle))
            transformation = transformation.translatedBy(x: 40, y: 0)
            images[index].transform = transformation
            
            let tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.tapGesture(_:)))
            view.addGestureRecognizer(tapGesture)
        }
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    
    func bind(_ genres: [GenreMusic]) {
        if genres.isEmpty {
            return
        }
        self.genres = genres
        for (index, imageView) in images.enumerated() {
            let genre = genres[index]
            imageView.image = genre.getImage()
            views[index].backgroundColor = genre.uiBgColor()
            labels[index].text = genre.title
        }
    }
    
    @objc func tapGesture(_ sender: UITapGestureRecognizer) {
        guard let index = sender.view?.tag else { return }
        let genre = genres[index]
        sender.view?.showAnimation {
            print("Clicked \(genre.title)")
            self.delegate?.didTapGenre(genre)
        }
        
    }
    
}

extension GenreMusic {
    
    func uiBgColor() -> UIColor {
        return hexStringToUIColor(hex: self.backgroundColor)
    }
    
    func getImage() -> UIImage? {
        return UIImage(named: imageName)
    }
}
