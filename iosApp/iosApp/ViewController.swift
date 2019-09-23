import UIKit
import app

class ViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource  {
    
    @IBOutlet weak var pokemonView: UIImageView!
    @IBOutlet weak var textField: UITextField!
    @IBOutlet weak var picker: UIPickerView!
    @IBOutlet weak var button: UIButton!
    @IBOutlet weak var answerLabel: UILabel!
    var alert : UIAlertController!
    
    internal var api = PokemonApi()
    var pokemonSprit : UIImage!
    var pokemon: Pokemon? = nil
    var wasAnswered = false
    var selectedGenaration = Genaration.g1
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        searchPokemon()
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        //        let list = Genaration.Companion.init().getArray()
        
        textField.autocorrectionType = .no
        
        self.picker.delegate = self
        self.picker.dataSource = self
        
        textField.backgroundColor = UIColor(red:0.86, green:0.08, blue:0.08, alpha:1.0)
        
        alert = UIAlertController(title: nil, message: "Please wait...", preferredStyle: .alert)
        
        let loadingIndicator = UIActivityIndicatorView(frame: CGRect(x: 10, y: 5, width: 50, height: 50))
        loadingIndicator.hidesWhenStopped = true
        loadingIndicator.activityIndicatorViewStyle = UIActivityIndicatorView.Style.gray
        loadingIndicator.startAnimating();
        alert.view.addSubview(loadingIndicator)
    }
    
    private func searchPokemon(){
        present(alert!, animated: true, completion: nil)
        let pokemonNumber = Int32.random(in: 1..<selectedGenaration.maxNumber)
        api.getPokemon(pokemonId: pokemonNumber,
                       success: { data in
                        self.pokemonSuccess(p: data)
                        return KotlinUnit()
        }, failure: {
            self.handleError($0?.message)
            return KotlinUnit()
        })
    }
    
    
    internal func pokemonSuccess(p: Pokemon) {
        pokemon = p
        api.getPokemonSprite(sprite: p.sprites,
                             success: { image in
                                if let img = image {
                                    self.spriteSuccess(image: img)
                                }
                                return KotlinUnit()
        }, failure: {
            self.handleError($0?.message)
            return KotlinUnit()
        })
    }
    
    internal func spriteSuccess(image : UIImage){
        pokemonSprit = image
        self.pokemonView.image = image.imageWithColor(color: UIColor(red:0.05, green:0.36, blue:0.64, alpha:1.0))
        alert.dismiss(animated: false, completion: nil)

    }
    
    internal func handleError(_ error: String?){
        let message = error ?? "An unknown error occurred. Retry?"
        let alert = UIAlertController(title: "Error", message: message, preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "Retry", style: .default, handler: { action in
            self.searchPokemon()
        }))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        
        self.present(alert, animated: true)
        dismiss(animated: false, completion: nil)

    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return Int(Genaration.Companion.init().getArray().size)
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return (Genaration.Companion.init().getArray().get(index: Int32(row)) as! Genaration).geName
    }

    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int){
        selectedGenaration = (Genaration.Companion.init().getArray().get(index: Int32(row)) as! Genaration)
    }
    
    
    @IBAction func buttonClick(_ sender: Any) {
        if let p = pokemon {
            if (!wasAnswered) {
                if (textField.text?.caseInsensitiveCompare(p.name) == .orderedSame){
                    answerLabel.textColor = UIColor(red:0.16, green:0.30, blue:0.02, alpha:1.0)
                    answerLabel.text = "CORRECT"
                } else {
                    answerLabel.textColor = UIColor(red:0.46, green:0.00, blue:0.00, alpha:1.0)
                    textField.text = p.name.uppercased()
                    answerLabel.text = "WRONG"
                }
                pokemonView.image = pokemonSprit
                button.setTitle("Next" , for: .normal)
                textField.isEnabled = false
                answerLabel.isHidden = false
            } else {
                answerLabel.isHidden = true
                searchPokemon()
                textField.text = ""
                textField.isEnabled = true
                button.setTitle("Check" , for: .normal)
            }
        }
        wasAnswered = !wasAnswered
    }
    
}

extension UIImage {
    func imageWithColor(color: UIColor) -> UIImage? {
        var image = withRenderingMode(.alwaysTemplate)
        UIGraphicsBeginImageContextWithOptions(size, false, scale)
        color.set()
        image.draw(in: CGRect(x: 0, y: 0, width: size.width, height: size.height))
        image = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()
        return image
    }
}
