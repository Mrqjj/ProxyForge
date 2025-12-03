package com.proxy.forge.service.impl;

import com.proxy.forge.api.pojo.QueryById;
import com.proxy.forge.api.pojo.SaveGlobalReplace;
import com.proxy.forge.dto.GlobalReplace;
import com.proxy.forge.repository.GlobalReplaceRepository;
import com.proxy.forge.service.GlobalReplaceService;
import com.proxy.forge.vo.ResponseApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.service.impl</p>
 * <p>Description: 全局拦截实现类</p>
 * <p>Copyright: Copyright (c) 2025 by Ts</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-27 16:32
 **/
@Slf4j
@Service
public class GlobalReplaceServiceImpl implements GlobalReplaceService {


    @Autowired
    GlobalReplaceRepository globalReplaceRepository;


    @Override
    public boolean initGlobalReplace() {
        GlobalReplace globalReplace = new GlobalReplace();
        globalReplace.setUrlPattern("/good.html");
        globalReplace.setContentType("text/html");
        globalReplace.setResponseContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>The Unintentional Empire: An Analysis of American Global Influence</title>\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n" +
                "    <link\n" +
                "        href=\"https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700&family=Source+Sans+3:wght@400;600&display=swap\"\n" +
                "        rel=\"stylesheet\">\n" +
                "    <style>\n" +
                "        /* Modern CSS Reset */\n" +
                "        *,\n" +
                "        *::before,\n" +
                "        *::after {\n" +
                "            box-sizing: border-box;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        /* General Body Styling */\n" +
                "        body {\n" +
                "            font-family: 'Source Sans 3', sans-serif;\n" +
                "            line-height: 1.8;\n" +
                "            background-color: #f4f4f9;\n" +
                "            color: #333;\n" +
                "            -webkit-font-smoothing: antialiased;\n" +
                "            -moz-osx-font-smoothing: grayscale;\n" +
                "        }\n" +
                "\n" +
                "        /* Main Container for the Blog Post */\n" +
                "        .container {\n" +
                "            max-width: 900px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 2rem 1.5rem;\n" +
                "        }\n" +
                "\n" +
                "        /* Blog Post Article Styling */\n" +
                "        .blog-post {\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 12px;\n" +
                "            box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);\n" +
                "            overflow: hidden;\n" +
                "            margin-top: 2.5rem;\n" +
                "        }\n" +
                "\n" +
                "        /* Header Section */\n" +
                "        .blog-header {\n" +
                "            text-align: center;\n" +
                "            padding: 3rem 1.5rem 2rem;\n" +
                "        }\n" +
                "\n" +
                "        .blog-title {\n" +
                "            font-family: 'Playfair Display', serif;\n" +
                "            font-size: 3rem;\n" +
                "            font-weight: 700;\n" +
                "            line-height: 1.2;\n" +
                "            color: #1a1a1a;\n" +
                "            margin-bottom: 0.5rem;\n" +
                "        }\n" +
                "\n" +
                "        .blog-subtitle {\n" +
                "            font-family: 'Source Sans 3', sans-serif;\n" +
                "            color: #6c757d;\n" +
                "            font-size: 1.15rem;\n" +
                "            font-weight: 400;\n" +
                "        }\n" +
                "\n" +
                "        /* Hero Image with Interactive Effect */\n" +
                "        .hero-image-container {\n" +
                "            padding: 1.5rem;\n" +
                "            background-color: #ffffff;\n" +
                "        }\n" +
                "\n" +
                "        .hero-image {\n" +
                "            width: 100%;\n" +
                "            height: auto;\n" +
                "            display: block;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);\n" +
                "            transition: transform 0.2s ease-out, box-shadow 0.3s ease;\n" +
                "        }\n" +
                "\n" +
                "        /* Main Content Area */\n" +
                "        .blog-content {\n" +
                "            padding: 2.5rem 3.5rem 3rem;\n" +
                "        }\n" +
                "\n" +
                "        .blog-content h2 {\n" +
                "            font-family: 'Playfair Display', serif;\n" +
                "            font-size: 2rem;\n" +
                "            font-weight: 700;\n" +
                "            margin-top: 2.8rem;\n" +
                "            margin-bottom: 1.2rem;\n" +
                "            padding-bottom: 0.6rem;\n" +
                "            border-bottom: 1px solid #e9ecef;\n" +
                "            color: #1a1a1a;\n" +
                "        }\n" +
                "\n" +
                "        .blog-content h2:first-of-type {\n" +
                "            margin-top: 0;\n" +
                "        }\n" +
                "\n" +
                "        .blog-content p {\n" +
                "            font-size: 1.1rem;\n" +
                "            margin-bottom: 1.5rem;\n" +
                "            color: #454545;\n" +
                "        }\n" +
                "\n" +
                "        .blog-content strong {\n" +
                "            color: #000;\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "\n" +
                "        /* Footer Styling */\n" +
                "        .page-footer {\n" +
                "            text-align: center;\n" +
                "            padding: 2.5rem;\n" +
                "            margin-top: 2rem;\n" +
                "            font-family: 'Source Sans 3', sans-serif;\n" +
                "            font-size: 0.95rem;\n" +
                "            color: #888;\n" +
                "        }\n" +
                "\n" +
                "        /* Responsive Design */\n" +
                "        @media (max-width: 768px) {\n" +
                "            .blog-title {\n" +
                "                font-size: 2.5rem;\n" +
                "            }\n" +
                "\n" +
                "            .blog-content {\n" +
                "                padding: 2rem 1.5rem;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\n" +
                "    <div class=\"container\">\n" +
                "        <header class=\"blog-header\">\n" +
                "            <p class=\"blog-subtitle\">An External, Objective Analysis</p>\n" +
                "            <h1 class=\"blog-title\">The Unintentional Empire</h1>\n" +
                "        </header>\n" +
                "\n" +
                "        <main>\n" +
                "            <article class=\"blog-post\">\n" +
                "                <div class=\"hero-image-container\">\n" +
                "                    <img src=\"https://i.postimg.cc/0NrVgGtj/Gemini-Generated-Image-gtdub4gtdub4gtdu.png\"\n" +
                "                        alt=\"showing both progress and dependency.\" class=\"hero-image\">\n" +
                "                </div>\n" +
                "\n" +
                "                <div class=\"blog-content\">\n" +
                "                    <h2>A Dialectical Introduction</h2>\n" +
                "                    <p>From an external viewpoint, the American project presents a fascinating paradox. It is a nation\n" +
                "                        founded on explicit ideals of liberty, democracy, and individual sovereignty, yet its most\n" +
                "                        profound global impact has been delivered not through philosophical treatise, but through the\n" +
                "                        overwhelming, often impersonal, force of its economic and productive might. The narrative of the\n" +
                "                        20th and 21st centuries is inextricably linked to how the United States leveraged capitalism,\n" +
                "                        not merely as an internal economic system, but as a dynamic, world-altering engine. This engine,\n" +
                "                        fueled by immense productivity and technological innovation, simultaneously promoted a vision of\n" +
                "                        a freer, more prosperous world while forging new and complex systems of dependency. To\n" +
                "                        understand the current global landscape is to analyze the stages through which this economic\n" +
                "                        power radiated outwards, shaping nations, markets, and the very fabric of society in a process\n" +
                "                        that was less a deliberate conquest and more the inexorable consequence of a system built for\n" +
                "                        expansion.</p>\n" +
                "\n" +
                "                    <h2>Phase I: The Architect of Order (c. 1945–1971)</h2>\n" +
                "                    <p>The conclusion of the Second World War left the world fractured and its great powers, save one,\n" +
                "                        in ruins. The United States emerged not just as a military victor, but as the sole industrial\n" +
                "                        power with its infrastructure intact and its productive capacity supercharged. This unique\n" +
                "                        position allowed it to transition from the \"arsenal of democracy\" to the architect of a new\n" +
                "                        global economic order. From the outside, this phase was defined by two monumental initiatives:\n" +
                "                        the Marshall Plan and the Bretton Woods system. The Marshall Plan was a masterstroke of economic\n" +
                "                        statecraft. By injecting massive capital into Western Europe, the U.S. simultaneously rebuilt\n" +
                "                        devastated economies and contained the ideological spread of communism. More importantly, it\n" +
                "                        cultivated future markets for American goods, creating a transatlantic economic ecosystem\n" +
                "                        dependent on American industrial output and consumption. Concurrently, the Bretton Woods\n" +
                "                        Agreement established a global financial architecture with the U.S. dollar, pegged to gold, at\n" +
                "                        its center. The creation of the International Monetary Fund (IMF) and the World Bank—largely\n" +
                "                        funded and influenced by the U.S.—provided the mechanisms to stabilize this system. For nations\n" +
                "                        outside the Soviet sphere, integration into this framework was the only viable path to\n" +
                "                        reconstruction and growth. This stage was not about exporting democracy through ballots, but\n" +
                "                        through blueprints and balance sheets.</p>\n" +
                "\n" +
                "                    <h2>Phase II: The Catalyst for Globalization (c. 1971–1989)</h2>\n" +
                "                    <p>This era began with a seismic shock: the U.S. unilaterally dismantling the Bretton Woods system\n" +
                "                        by decoupling the dollar from gold in 1971. While internally a response to domestic economic\n" +
                "                        pressures, its external effect was to unmoor the global economy, ushering in an age of floating\n" +
                "                        exchange rates and, critically, financialization. This period saw the rise of a new economic\n" +
                "                        orthodoxy, often termed the \"Washington Consensus.\" Through the influence of the IMF and World\n" +
                "                        Bank, the American model of deregulation, privatization, and free trade was promoted globally as\n" +
                "                        the universal prescription for economic development. From an outside-in perspective, this was\n" +
                "                        the phase where American capitalism began to operate as a truly globalizing force. U.S.-based\n" +
                "                        multinational corporations expanded aggressively, seeking efficiencies in labor and resources\n" +
                "                        abroad. This catalyzed the creation of global supply chains, integrating economies across\n" +
                "                        continents in a production process whose nerve center remained in the United States. The world\n" +
                "                        was being rewired not by diplomats, but by a corporate and financial logic that prioritized\n" +
                "                        efficiency and shareholder value.</p>\n" +
                "\n" +
                "                    <h2>Phase III: The Unipolar Vanguard of the Digital Age (c. 1989–2008)</h2>\n" +
                "                    <p>The collapse of the Soviet Union left the United States as the world’s sole superpower. This\n" +
                "                        \"unipolar moment\" saw the ideological triumph of liberal-democratic capitalism, with its\n" +
                "                        American variant as the de facto standard. The key development of this era, however, was the\n" +
                "                        commercialization and globalization of a technology born from U.S. military research: the\n" +
                "                        internet. Externally, the U.S. became the vanguard of the digital revolution. Companies like\n" +
                "                        Microsoft, Intel, Cisco, and later Google and Amazon, built the foundational layers of the new\n" +
                "                        global economy. Silicon Valley, with its unique ecosystem of venture capital and disruptive\n" +
                "                        innovation, became a global magnet for talent. This was not just about exporting products; it\n" +
                "                        was about exporting a new paradigm of productivity. This technological dominance was a powerful\n" +
                "                        amplifier of American soft power, as American software platforms, social media, and\n" +
                "                        entertainment became globally ubiquitous, shaping cultural norms and consumer aspirations.</p>\n" +
                "\n" +
                "                    <h2>Phase IV: The Challenged Incumbent in a Multipolar System (c. 2008–Present)</h2>\n" +
                "                    <p>The 2008 Global Financial Crisis marked a critical turning point. Originating from within the\n" +
                "                        heart of the U.S. financial system, the crisis exposed deep vulnerabilities in the model of\n" +
                "                        deregulated capitalism. For the first time, the credibility of the American-led economic order\n" +
                "                        was severely damaged. This crisis accelerated a power shift that was already underway. The\n" +
                "                        primary beneficiary was China, whose state-directed model of capitalism had allowed it to become\n" +
                "                        the world's manufacturing workshop. It transitioned from a participant in the American-led\n" +
                "                        system to a challenger, launching its own initiatives like the Belt and Road Initiative. In this\n" +
                "                        current phase, the United States is a challenged incumbent. Its technological leadership is now\n" +
                "                        contested. The very platforms that fueled its global influence are now arenas of geopolitical\n" +
                "                        competition. The logic of maximum efficiency is being re-evaluated in favor of national\n" +
                "                        resilience and security, signaling a potential fragmentation of the very globalized system\n" +
                "                        America once championed.</p>\n" +
                "\n" +
                "                    <h2>A Dialectical Conclusion</h2>\n" +
                "                    <p>The American-led epoch has concluded with a profound dialectic. The push for a globalized, open,\n" +
                "                        and interconnected world, driven by the engine of American productivity and capital, was\n" +
                "                        immensely successful. It unleashed technological progress, integrated economies, and lifted\n" +
                "                        billions from poverty. Yet, this same system has yielded its own antithesis. It has generated\n" +
                "                        vast inequalities and created new forms of dependency. The global system that America built has,\n" +
                "                        in its success, equipped other nations with the means to challenge its primacy. The world is now\n" +
                "                        left to grapple with the paradoxical legacy of this unintentional empire: a planet more\n" +
                "                        interconnected and productive than ever, yet more fractured and contentious, wrestling with\n" +
                "                        whether the tools of capitalism can be wielded to serve a more equitable and sovereign vision of\n" +
                "                        the global future.</p>\n" +
                "                </div>\n" +
                "            </article>\n" +
                "        </main>\n" +
                "\n" +
                "        <footer class=\"page-footer\">\n" +
                "            <p>&copy; 2025. Generated for illustrative purposes.</p>\n" +
                "        </footer>\n" +
                "    </div>\n" +
                "\n" +
                "    <script>\n" +
                "        document.addEventListener('DOMContentLoaded', function () {\n" +
                "            const heroImage = document.querySelector('.hero-image');\n" +
                "            if (!heroImage) return;\n" +
                "\n" +
                "            heroImage.addEventListener('mousemove', (e) => {\n" +
                "                const rect = heroImage.getBoundingClientRect();\n" +
                "                const x = e.clientX - rect.left;\n" +
                "                const y = e.clientY - rect.top;\n" +
                "\n" +
                "                const centerX = rect.width / 2;\n" +
                "                const centerY = rect.height / 2;\n" +
                "\n" +
                "                const rotateX = (centerY - y) / 25; // Invert for natural feel\n" +
                "                const rotateY = (x - centerX) / 25;\n" +
                "\n" +
                "                heroImage.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.03)`;\n" +
                "            });\n" +
                "\n" +
                "            heroImage.addEventListener('mouseleave', () => {\n" +
                "                heroImage.style.transform = 'perspective(1000px) rotateX(0deg) rotateY(0deg) scale(1)';\n" +
                "            });\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "\n" +
                "</html>");
        if (globalReplaceRepository.count() == 0) {
            return globalReplaceRepository.save(globalReplace).getId() > 0;
        } else {
            log.info("[初始化拦截替换]  非首次启动,不需要初始化.");
            return false;
        }
    }

    /**
     * 获取全局拦截器配置信息。
     *
     * @param request  HttpServletRequest对象，用于处理客户端请求
     * @param response HttpServletResponse对象，用于响应客户端
     * @return 返回一个包含状态码、提示信息、全局拦截器列表及总数的ResponseApi对象
     */
    @Override
    public Object getGlobalReplace(HttpServletRequest request, HttpServletResponse response) {
        List<GlobalReplace> all = globalReplaceRepository.findAll();
        return new ResponseApi(200, "成功~", all, globalReplaceRepository.count());
    }

    /**
     * 保存或更新全局替换配置。
     *
     * @param saveGlobalReplace 包含要保存的全局替换配置信息的对象。
     * @param request           服务器接收到的HTTP请求对象。
     * @param response          服务器发送回客户端的HTTP响应对象。
     * @return 返回一个表示操作结果的对象。
     */
    @Override
    public Object saveGlobalReplace(SaveGlobalReplace saveGlobalReplace, HttpServletRequest request, HttpServletResponse response) {
        GlobalReplace globalReplace = new GlobalReplace();
        globalReplace.setUrlPattern(saveGlobalReplace.getUrlPattern());
        globalReplace.setContentType(saveGlobalReplace.getContentType());
        globalReplace.setResponseContent(saveGlobalReplace.getResponseContent());
        int result = 0;
        String successMessage = "";
        String failMessage = "";
        // 存在更新
        if (globalReplaceRepository.findGlobalReplaceByUrlPattern(saveGlobalReplace.getUrlPattern()) != null) {
            result = globalReplaceRepository.updateGlobalReplaceByUrlPattern(globalReplace);
            successMessage = "更新成功";
            failMessage = "更新失败";
        } else {
            result = globalReplaceRepository.save(globalReplace).getId();
            successMessage = "添加成功";
            failMessage = "添加失败";
        }
        if (result > 0) {
            return new ResponseApi(200, successMessage, true);
        } else {
            return new ResponseApi(201, failMessage, false);
        }
    }

    /**
     * 根据提供的URI获取全局替换配置。
     *
     * @param uri 用于查找全局替换配置的URL模式
     * @return 匹配给定URL模式的GlobalReplace实体，如果没有找到匹配项则返回null
     */
    @Override
    public GlobalReplace getGlobalReplace(String uri) {
        return globalReplaceRepository.findGlobalReplaceByUrlPattern(uri);
    }

    /**
     * 根据提供的删除条件进行全局替换作。
     *
     * @param deleteById 删除哪些记录的标准，通常包括身份识别或其他身份信息。
     * @return 一个表示删除作结果的对象，可以是确认、状态或受影响的记录计数。
     */
    @Override
    public Object deleteGlobalReplace(QueryById deleteById) {
        globalReplaceRepository.deleteById(deleteById.getId());
        return new ResponseApi(200, "成功", null);
    }
}
