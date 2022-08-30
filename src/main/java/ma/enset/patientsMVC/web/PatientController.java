package ma.enset.patientsMVC.web;

import lombok.AllArgsConstructor;
import ma.enset.patientsMVC.entities.Patient;
import ma.enset.patientsMVC.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;

    //Afficher la Liste des patients
    @GetMapping(path = "/index")
    public String patients(Model model,
                            @RequestParam(name="page",defaultValue = "0") int page,
                            @RequestParam(name="size",defaultValue = "5") int size,
                           @RequestParam(name="keyword",defaultValue = "") String keyword) {
        Page<Patient> pagePatients = patientRepository.findByNomContains(keyword,PageRequest.of(page,size));
        model.addAttribute("ListPatients",pagePatients.getContent());
        model.addAttribute("pages",new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("currentSize",size);
        model.addAttribute("keyword",keyword);
        return "patients";
    }

    @GetMapping(path = "/")
    public String home() {
        return "redirect:/index";
    }

    //Supprimer un patient dans la liste
    @GetMapping(path = "/delete")
    public String delete(Long id,int page,int size,String keyword) {
        patientRepository.deleteById(id);
        return "redirect:/index?page="+page+"&size="+size+"&keyword="+keyword;
    }

    //Ajouter un patient a la liste
    @GetMapping("/formPatients")
    public String formPatients(Model model) {
        model.addAttribute("patient",new Patient());
        return "formPatients";
    }

    @PostMapping(path = "/save")
    public String save(Model model, @Valid Patient patient, BindingResult bindingResult){
        if (bindingResult.hasErrors()) return "formPatients";
        patientRepository.save(patient);
        return "redirect:/index";
    }

    //Modifier un patient dans la liste
    @GetMapping("/editPatients")
    public String editPatients(Model model,Long id,int page,int size,String keyword) {
        Patient patient = patientRepository.findById(id).orElse(null);
        if (patient==null)throw  new RuntimeException("Patient introuvable");
        model.addAttribute("patient",patient);
        model.addAttribute("page",page);
        model.addAttribute("size",size);
        model.addAttribute("keyword",keyword);
        return "editPatients";
    }

    @PostMapping(path = "/edit")
    public String edit(Model model, @Valid Patient patient, BindingResult bindingResult,int page,int size,String keyword){
        if (bindingResult.hasErrors()) return "editPatients";
        patientRepository.save(patient);
        return "redirect:/index?page="+page+"&size="+size+"&keyword="+keyword;
    }






    //pour envoyer les donnee ou format json
    @GetMapping("/patients")
    @ResponseBody
    public List<Patient> patientList(){
        return patientRepository.findAll();
    }
}
