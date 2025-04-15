package travel.travel_Spring.Controller.functionController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FunctionController {

    @GetMapping("/joinMembership")
    public String years(Model model) {
        int currentYear = LocalDate.now().getYear();

        List<Integer> years = new ArrayList<>();
        for(int i = currentYear; i >= 1950; i--) {
            years.add(i);
        }

        List<Integer> months = new ArrayList<>();
        for(int i = 1; i <= 12; i++) {
            months.add(i);
        }

        List<Integer> days = new ArrayList<>();
        for(int day = 1; day <= 31; day++) {
            days.add(day);
        }

        model.addAttribute("years", years);
        model.addAttribute("months", months);
        model.addAttribute("days", days);
        return "joinMembership";
    }
}
