import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class FeignClientEagerLoadConfig implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor, ApplicationListener<ApplicationStartedEvent> {

	private ApplicationContext applicationContext;

	private BeanDefinitionRegistry beanDefinitionRegistry;

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
		this.beanDefinitionRegistry = registry;
	}

	@Override
	public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
		CachingSpringLoadBalancerFactory loadBalancerFactory = applicationContext.getBean(CachingSpringLoadBalancerFactory.class);
		String[] beanNames = applicationContext.getBeanNamesForAnnotation(FeignClient.class);
		for (String name : beanNames) {
			Object feignClientNameObj = beanDefinitionRegistry.getBeanDefinition(name).getPropertyValues().get("name");
			if (feignClientNameObj != null) {
				loadBalancerFactory.create(feignClientNameObj.toString());
			}
		}
	}
}
