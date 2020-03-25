package co.grandcircus.CO2Competition.Controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.grandcircus.CO2Competition.Objects.Company;
import co.grandcircus.CO2Competition.Objects.Employee;
import co.grandcircus.CO2Competition.Repos.CompanyRepo;
import co.grandcircus.CO2Competition.Repos.EmployeeRepo;

@Controller
public class LoginController {
	@Autowired
	private HttpSession sesh;
	@Autowired
	private EmployeeRepo emRepo;
	@Autowired
	private CompanyRepo coRepo;

	@RequestMapping("/login")
	public ModelAndView showLogin() {
		return new ModelAndView("login/login");
	}

	@PostMapping("/login")
	public ModelAndView checkLogin(@RequestParam("username") String username, @RequestParam("password") String password,
			RedirectAttributes red) {

		Employee employee = emRepo.findByUsernameIgnoreCase(username);

		if (employee == null || !password.equals(employee.getPassword())) {
			red.addFlashAttribute("message", "Incorrect username or password, please try again!");
			red.addFlashAttribute("messageType", "danger");
			return new ModelAndView("redirect:/login");
		}

		sesh.setAttribute("employee", employee);
//		System.out.println(employee.isAdmin());
		return new ModelAndView("redirect:/dashboard");
	}

	@RequestMapping("/logout")
	public ModelAndView showLogout(RedirectAttributes red) {
		sesh.invalidate();
		red.addFlashAttribute("message", "Successfully logged out.");
		red.addFlashAttribute("messageType", "success");
		return new ModelAndView("redirect:/login");
	}

	@RequestMapping("/register")
	public ModelAndView showReg(Company company, Employee employee) {
		ModelAndView mav = new ModelAndView("login/employee-registration");

		mav.addObject("companies", coRepo.findAll());
		mav.addObject("vehicleTypes", emRepo.findAllVehicleType());

		return mav;
	}

	@PostMapping("/register")
	public ModelAndView submitReg(Employee employee, RedirectAttributes red) {
		List<Employee> employeeList = emRepo.findByCompanyName(employee.getCompany().getName());

		if (employeeList.size() == 0 || employeeList == null) {
			employee.setCompanyAdmin(employee.getCompany());
			Company company = coRepo.getOne(employee.getCompany().getCompanyId());
			company.setAdmin(employee);

		}

		emRepo.save(employee);

//		coRepo.save(company);
		red.addFlashAttribute("msg", "Thank you for registering with us, " + employee.getName());

		return new ModelAndView("redirect:/login");
	}

	@RequestMapping("/registercompany")
	public ModelAndView showCompanyReg() {
		ModelAndView mav = new ModelAndView("login/registercompany");
		return mav;
	}

	@PostMapping("/registercompany")
	public ModelAndView submitCompany(Company company, RedirectAttributes red) {
		coRepo.save(company);
//		coRepo.save(company);
		red.addFlashAttribute("msg", "Enjoy your day, " + company.getName());

		return new ModelAndView("redirect:/login");
	}

	// Shows form for user to update settings
	@RequestMapping("/updateuser")
	public ModelAndView updateUser() {
		// Get logged in employee
		Employee employee = (Employee) sesh.getAttribute("employee");

		ModelAndView mav = new ModelAndView("login/employee-update");
		mav.addObject("companies", coRepo.findAll());
		mav.addObject("vehicleTypes", emRepo.findAllVehicleType());

		// CHECK IF USER IS ADMIN
//		Employee user = (Employee) sesh.getAttribute("employee");
//		Employee admin = user.getCompany().getAdmin();
//		if (user.getUsername().equals(admin.getUsername())) {
		mav.addObject("admin", "true");
		mav.addObject("employeeList", emRepo.findByCompanyName(employee.getCompany().getName()));
//		}

		return mav;
	}

	// handles admin feature to edit another employee
	@PostMapping("/updateadmin")
	public ModelAndView updateAdmin(@RequestParam Long id) {
		ModelAndView mav = new ModelAndView("login/employee-update");
		Employee employee = emRepo.findById(id).orElse(null);
		mav.addObject("employee", employee);
		mav.addObject("companies", coRepo.findAll());
		mav.addObject("vehicleTypes", emRepo.findAllVehicleType());
		// CHECK IF USER IS ADMIN
//		Employee user = (Employee) sesh.getAttribute("employee");
//		Employee admin = user.getCompany().getAdmin();
//		if (user.getUsername().equals(admin.getUsername())) {
		mav.addObject("admin", "true");
		mav.addObject("employeeList", emRepo.findByCompanyName(employee.getCompany().getName()));
//		}
		return mav;
	}

	// Handles admin change
	@PostMapping("/newadmin")
	public ModelAndView newAdmin(@RequestParam("id") Long id, RedirectAttributes redir) {
		Employee newAdmin = emRepo.findById(id).orElse(null);
		Company company = newAdmin.getCompany();
//		if (company.setAdmin(newAdmin){
		redir.addFlashAttribute("message", "Sucessfully changed admin to " + newAdmin.getName());
		redir.addFlashAttribute("messageType", "success");
//		} else {
//			redir.addFlashAttribute("message", "An error has ocurred, please try again.");
//			redir.addFlashAttribute("messageType", "warning");
//		}

		return new ModelAndView("redirect:/updateuser");
	}

	// Handles post request and redirects with appropriate message
	// if passwords do not match, if current password does not match,
	// or if user was successfully updated
	@PostMapping("/updateuser")
	public ModelAndView submitUpdateUser(@RequestParam String current,
			@RequestParam(required = false) String passwordConfirm, Employee updatedEmployee,
			RedirectAttributes redir) {
		Employee employee = (Employee) sesh.getAttribute("employee");
		// if password matches
		if (!current.equals(employee.getPassword())) {
			redir.addFlashAttribute("message", "Unable to confirm password.");
			redir.addFlashAttribute("messageType", "danger");
			return new ModelAndView("redirect:/updateuser");
		}
		if (!updatedEmployee.getPassword().isBlank() || updatedEmployee.getPassword() == null) {
			if (!updatedEmployee.getPassword().equals(passwordConfirm)) {
				redir.addFlashAttribute("message", "Passwords do not match.");
				redir.addFlashAttribute("messageType", "warning");
				return new ModelAndView("redirect:/updateuser");
			}
		} else {
			updatedEmployee.setPassword(employee.getPassword());
		}

		// update
		emRepo.update(updatedEmployee.getCity(), updatedEmployee.getName(), updatedEmployee.getPassword(),
				updatedEmployee.getStreetAddress(), updatedEmployee.getUsername(), updatedEmployee.getZipCode(),
				updatedEmployee.getCompany().getCompanyId(), updatedEmployee.getVehicleType(),
				updatedEmployee.getEmployeeId());
		sesh.removeAttribute("employee");
		sesh.setAttribute("employee", updatedEmployee);
		// redirect
		redir.addFlashAttribute("message", "Changes confirmed!");
		redir.addFlashAttribute("messageType", "success");
		return new ModelAndView("redirect:/updateuser");
	}

}
