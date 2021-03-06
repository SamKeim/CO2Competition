package co.grandcircus.CO2Competition.Controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.grandcircus.CO2Competition.ApiService;
import co.grandcircus.CO2Competition.Entities.Distance;
import co.grandcircus.CO2Competition.Entities.SearchResult;
import co.grandcircus.CO2Competition.Objects.Company;
import co.grandcircus.CO2Competition.Objects.Employee;
import co.grandcircus.CO2Competition.Repos.CompanyRepo;
import co.grandcircus.CO2Competition.Repos.EmployeeRepo;

@Controller
public class SettingsController {

	@Autowired
	private HttpSession sesh;
	@Autowired
	private EmployeeRepo emRepo;
	@Autowired
	private CompanyRepo coRepo;
	@Autowired
	private ApiService apiServe;
	
	
	// Shows form for user to update settings
	@RequestMapping("/updateuser")
	public ModelAndView updateUser() {
		
		Employee user = (Employee) sesh.getAttribute("employee");

		ModelAndView mav = new ModelAndView("settings/employee-update");
		mav.addObject("companies", coRepo.findAll());
		mav.addObject("vehicleTypes", emRepo.findAllVehicleType());
		mav.addObject("employeeToEdit", user);
		
		// CHECK IF USER IS ADMIN
	
		if (user.isAdmin()) {
			mav.addObject("admin", "true");
			mav.addObject("employeeList", emRepo.findByCompanyName(user.getCompany().getName()));
		}		
		return mav;
	}
	
	// handles admin feature to edit another employee
	@PostMapping("/updateadmin")
	public ModelAndView updateAdmin(@RequestParam Long id) {
		ModelAndView mav = new ModelAndView("settings/employee-update");
		Employee employee = emRepo.findById(id).orElse(null);
		mav.addObject("employeeToEdit", employee);
		mav.addObject("companies", coRepo.findAll());
		mav.addObject("vehicleTypes", emRepo.findAllVehicleType());

		// CHECK IF USER IS ADMIN
		Employee user = (Employee) sesh.getAttribute("employee");
		if (user.isAdmin()) {
			mav.addObject("admin", "true");
			mav.addObject("employeeList", emRepo.findByCompanyName(employee.getCompany().getName()));
		}
		return mav;
	}

	// Handles admin change
	@PostMapping("/newadmin")
	public ModelAndView newAdmin(@RequestParam("id") Long id,
			@RequestParam String current,
			RedirectAttributes redir) {
		// if password does not match
		Employee employee = (Employee) sesh.getAttribute("employee");
		if (!current.equals(employee.getPassword())) {
			redir.addFlashAttribute("message", "Unable to confirm password.");
			redir.addFlashAttribute("messageType", "danger");
			return new ModelAndView("redirect:/updateuser");
		}
		
		// if password is correct, handle update
		Employee newAdmin = emRepo.findById(id).orElse(null);
		Company company = newAdmin.getCompany();
		company.setAdmin(newAdmin);
		coRepo.save(company);
		
		//refreshes session employee
		sesh.removeAttribute("employee");
		sesh.setAttribute("employee", emRepo.getOne(employee.getEmployeeId()));
		
		redir.addFlashAttribute("message", "Sucessfully changed admin to " + newAdmin.getName());
		redir.addFlashAttribute("messageType", "success");			
		return new ModelAndView("redirect:/updateuser");
	}
	
	// Handles post request and redirects with appropriate message
	// if passwords do not match, if current password does not match,
	// or if user was successfully updated
	@PostMapping("/updateuser")
	public ModelAndView submitUpdateUser(@RequestParam String current,
			@RequestParam(required = false) String passwordConfirm,
			Employee updatedEmployee,
			RedirectAttributes redir) {
		Employee employee = (Employee) sesh.getAttribute("employee");
		// if password does not match
		if (!current.equals(employee.getPassword())) {
			redir.addFlashAttribute("message", "Unable to confirm password.");
			redir.addFlashAttribute("messageType", "danger");
			return new ModelAndView("redirect:/updateuser");
		}
		if (!updatedEmployee.getPassword().isEmpty() || updatedEmployee.getPassword() == null) {
			if (!updatedEmployee.getPassword().equals(passwordConfirm)) {
				redir.addFlashAttribute("message", "Passwords do not match.");
				redir.addFlashAttribute("messageType", "warning");
				return new ModelAndView("redirect:/updateuser");
			}
		} else {
			updatedEmployee.setPassword(emRepo.getOne(updatedEmployee.getEmployeeId()).getPassword());
		}
		SearchResult result = apiServe.getResult(updatedEmployee.getAddress(), employee.getCompany().getAddress());
		Distance dist = apiServe.getDistance(result);
		if (dist == null) {
			ModelAndView mav = new ModelAndView("redirect:/updateuser");
			redir.addFlashAttribute("message", "Invalid Address");
			redir.addFlashAttribute("messageType", "warning");
			return mav;
		}else {
		
		// update
		emRepo.save(updatedEmployee);
	
		//refreshes session employee
		sesh.removeAttribute("employee");
		sesh.setAttribute("employee", emRepo.getOne(employee.getEmployeeId()));
		
		// redirect
		redir.addFlashAttribute("message", "Changes confirmed!");
		redir.addFlashAttribute("messageType", "success");
		return new ModelAndView("redirect:/updateuser");
		}
	}


}
